import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormArray } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ZonaService } from '../../services/zona.service';
import { Coordenadas } from '../../../coordenadas/models/coordenadas.model';
import { Zona } from '../../models/zona.model';
import { Barrio } from '../../../barrios/models/barrio.model';
import { BarrioService } from '../../../barrios/services/barrio.service';
import Swal from 'sweetalert2';
import * as L from 'leaflet';

@Component({
  selector: 'app-zona-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './zona-form.component.html',
})
export class ZonaFormComponent implements OnInit, AfterViewInit, OnDestroy {

  zonaForm: FormGroup;
  isEditMode = false;
  zonaId: number | null = null;
  barrios: Barrio[] = [];
  
  // Map related properties
  map: any;
  markers: L.Marker[] = [];
  polygon: L.Polygon | null = null;
  barrioPolygon: L.Polygon | null = null;
  showMap = true;
  showManualForm = false;
  selectedBarrio: Barrio | null = null;

  constructor(
    private fb: FormBuilder,
    private zonaService: ZonaService,
    private barrioService: BarrioService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.zonaForm = this.fb.group({
      color: ['', Validators.required],
      barrio: [null, Validators.required],
      coordenadas: this.fb.array([])
    });
  }

  compareById = (a: any, b: any) => a && b && a.id === b.id;

  ngOnInit(): void {
    this.zonaId = this.route.snapshot.params['id'];
    if (this.zonaId) {
      this.isEditMode = true;
    }

    // Load barrios first, then load zona data if editing
    this.loadBarrios();

    // Listen to barrio selection changes
    this.zonaForm.get('barrio')?.valueChanges.subscribe(barrio => {
      if (barrio) {
        this.selectedBarrio = barrio;
        this.onBarrioSelected();
      }
    });
  }

  ngAfterViewInit(): void {
    if (this.showMap) {
      setTimeout(() => {
        this.initializeMap();
        
        // If we're in edit mode and have data, show barrio and markers
        if (this.isEditMode && this.selectedBarrio && this.coordenadas.length > 0) {
          this.showBarrioOnMap();
          this.loadMarkersFromForm();
        }
      }, 100);
    }
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
    }
  }

  loadZonaData(): void {
    this.zonaService.findById(this.zonaId!).subscribe(data => {
      if (data.length > 0) {
        const zona = data[0];
        
        // Set form values
        this.zonaForm.patchValue({
          color: zona.color,
        });
        
        // Find and set the barrio
        if (zona.barrioId) {
          const selectedBarrio = this.barrios.find(b => b.id == zona.barrioId);
          if (selectedBarrio) {
            this.selectedBarrio = selectedBarrio;
            this.zonaForm.get('barrio')?.setValue(selectedBarrio);
          }
        }
        
        // Clear existing coordinates and add loaded ones
        this.clearCoordinates();
        zona.coordenadas.forEach(coord => {
          this.coordenadas.push(this.createCoordenadaGroup(coord));
        });

        // If map is already initialized, load markers and show barrio
        if (this.map) {
          this.showBarrioOnMap();
          this.loadMarkersFromForm();
        }
      }
    });
  }

  get coordenadas(): FormArray {
    return this.zonaForm.get('coordenadas') as FormArray;
  }

  createCoordenadaGroup(coord?: Coordenadas): FormGroup {
    return this.fb.group({
      latitud: [coord ? coord.latitud : '', [Validators.required, Validators.pattern('^-?[0-9]\\d*(\\.\\d+)?$')]],
      longitud: [coord ? coord.longitud : '', [Validators.required, Validators.pattern('^-?[0-9]\\d*(\\.\\d+)?$')]]
    });
  }

  loadBarrios(): void {
    this.barrioService.getAll().subscribe(data => {
      this.barrios = data;
      
      // If editing, load zona data after barrios are loaded
      if (this.isEditMode && this.zonaId) {
        this.loadZonaData();
      }
    });
  }

  initializeMap(): void {
    if (this.map) {
      this.map.remove();
    }

    // Default center (Buenos Aires)
    const defaultCenter: [number, number] = [-34.6118, -58.3960];
    
    this.map = L.map('zona-map').setView(defaultCenter, 12);

    // Add tile layer
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    // Add click event to map (only if barrio is selected)
    this.map.on('dblclick', (e: any) => {
      if (this.selectedBarrio) {
        if (this.isPointInsideBarrio(e.latlng.lat, e.latlng.lng)) {
          this.addMarker(e.latlng.lat, e.latlng.lng);
        } else {
          Swal.fire({
            title: '¡Atención!',
            text: 'La zona debe estar dentro del barrio seleccionado.',
            icon: 'warning',
            confirmButtonText: 'Entendido'
          });
        }
      } else {
        Swal.fire({
          title: '¡Atención!',
          text: 'Primero debes seleccionar un barrio.',
          icon: 'warning',
          confirmButtonText: 'Entendido'
        });
      }
    });

    // Show barrio and markers if they exist (for edit mode)
    if (this.selectedBarrio) {
      this.showBarrioOnMap();
    }
    
    if (this.isEditMode && this.coordenadas.length > 0) {
      this.loadMarkersFromForm();
    }
  }

  onBarrioSelected(): void {
    if (this.map && this.selectedBarrio) {
      this.showBarrioOnMap();
      
      // Only clear zona markers if we're not in edit mode or if changing barrio
      if (!this.isEditMode) {
        this.clearZonaMarkers();
        this.clearCoordinates();
      }
    }
  }

  showBarrioOnMap(): void {
    if (!this.map || !this.selectedBarrio) return;

    // Remove existing barrio polygon
    if (this.barrioPolygon) {
      this.map.removeLayer(this.barrioPolygon);
    }

    // Create barrio polygon
    const barrioCoords = this.selectedBarrio.coordenadas.map(coord => 
      [parseFloat(coord.latitud.toString()), parseFloat(coord.longitud.toString())] as [number, number]
    );

    this.barrioPolygon = L.polygon(barrioCoords, {
      color: '#007bff',
      weight: 3,
      opacity: 0.8,
      fillColor: '#007bff',
      fillOpacity: 0.1
    }).addTo(this.map);

    // Add popup to barrio
    this.barrioPolygon.bindPopup(`
      <div>
        <h6><strong>Barrio: ${this.selectedBarrio.name}</strong></h6>
        <p>Haz doble clic dentro del área para agregar puntos de la zona</p>
      </div>
    `);

    // Fit map to show barrio
    this.map.fitBounds(this.barrioPolygon.getBounds().pad(0.1));
  }

  isPointInsideBarrio(lat: number, lng: number): boolean {
    if (!this.selectedBarrio) return false;

    const point = L.latLng(lat, lng);
    const barrioCoords = this.selectedBarrio.coordenadas.map(coord => 
      [parseFloat(coord.latitud.toString()), parseFloat(coord.longitud.toString())] as [number, number]
    );

    // Create a temporary polygon to check if point is inside
    const tempPolygon = L.polygon(barrioCoords);
    
    // Use Leaflet's internal method to check if point is inside polygon
    const bounds = tempPolygon.getBounds();
    if (!bounds.contains(point)) {
      return false;
    }

    // More precise point-in-polygon test
    return this.pointInPolygon([lat, lng], barrioCoords);
  }

  pointInPolygon(point: [number, number], polygon: [number, number][]): boolean {
    const [x, y] = point;
    let inside = false;

    for (let i = 0, j = polygon.length - 1; i < polygon.length; j = i++) {
      const [xi, yi] = polygon[i];
      const [xj, yj] = polygon[j];

      if (((yi > y) !== (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi)) {
        inside = !inside;
      }
    }

    return inside;
  }

  addMarker(lat: number, lng: number): void {
    // Create marker
    const marker = L.marker([lat, lng], {
      draggable: true,
      icon: L.divIcon({
        className: 'zona-marker',
        html: `<div style="background-color: ${this.zonaForm.get('color')?.value || '#28a745'}; width: 12px; height: 12px; border-radius: 50%; border: 2px solid white; box-shadow: 0 0 4px rgba(0,0,0,0.5);"></div>`,
        iconSize: [12, 12],
        iconAnchor: [6, 6]
      })
    }).addTo(this.map);

    // Add marker to array
    this.markers.push(marker);
    const coord: Coordenadas = { latitud: lat, longitud: lng };
    
    // Add coordinate to form
    this.coordenadas.push(this.createCoordenadaGroup(coord));

    // Add drag event to marker with boundary check
    marker.on('drag', (e: any) => {
      const markerIndex = this.markers.indexOf(marker);
      const newLatLng = e.target.getLatLng();
      
      if (this.isPointInsideBarrio(newLatLng.lat, newLatLng.lng)) {
        this.updateCoordinateInForm(markerIndex, newLatLng.lat, newLatLng.lng);
        this.updatePolygon();
      } else {
        // Revert to previous position
        const coordGroup = this.coordenadas.at(markerIndex);
        const prevLat = parseFloat(coordGroup.get('latitud')?.value);
        const prevLng = parseFloat(coordGroup.get('longitud')?.value);
        marker.setLatLng([prevLat, prevLng]);
        
        Swal.fire({
          title: '¡Atención!',
          text: 'La zona debe permanecer dentro del barrio seleccionado.',
          icon: 'warning',
          timer: 2000,
          showConfirmButton: false
        });
      }
    });

    // Add popup with remove option
    marker.bindPopup(`
      <div>
        <p><strong>Punto ${this.markers.length}</strong></p>
        <p>Lat: ${lat.toFixed(6)}</p>
        <p>Lng: ${lng.toFixed(6)}</p>
        <button onclick="window.removeZonaMarker(${this.markers.length - 1})" class="btn btn-danger btn-sm">Eliminar</button>
      </div>
    `);

    // Update polygon
    this.updatePolygon();

    // Make remove function available globally
    (window as any).removeZonaMarker = (index: number) => this.removeMarker(index);
  }

  loadMarkersFromForm(): void {
    this.clearZonaMarkers();
    
    for (let i = 0; i < this.coordenadas.length; i++) {
      const coord = this.coordenadas.at(i).value;
      if (coord.latitud && coord.longitud) {
        const lat = parseFloat(coord.latitud);
        const lng = parseFloat(coord.longitud);
        
        const marker = L.marker([lat, lng], {
          draggable: true,
          icon: L.divIcon({
            className: 'zona-marker',
            html: `<div style="background-color: ${this.zonaForm.get('color')?.value || '#28a745'}; width: 12px; height: 12px; border-radius: 50%; border: 2px solid white; box-shadow: 0 0 4px rgba(0,0,0,0.5);"></div>`,
            iconSize: [12, 12],
            iconAnchor: [6, 6]
          })
        }).addTo(this.map);

        this.markers.push(marker);

        // Add drag event with boundary check
        marker.on('drag', (e: any) => {
          const markerIndex = this.markers.indexOf(marker);
          const newLatLng = e.target.getLatLng();
          
          if (this.isPointInsideBarrio(newLatLng.lat, newLatLng.lng)) {
            this.updateCoordinateInForm(markerIndex, newLatLng.lat, newLatLng.lng);
            this.updatePolygon();
          } else {
            // Revert to previous position
            const coordGroup = this.coordenadas.at(markerIndex);
            const prevLat = parseFloat(coordGroup.get('latitud')?.value);
            const prevLng = parseFloat(coordGroup.get('longitud')?.value);
            marker.setLatLng([prevLat, prevLng]);
          }
        });

        // Add popup
        marker.bindPopup(`
          <div>
            <p><strong>Punto ${i + 1}</strong></p>
            <p>Lat: ${lat.toFixed(6)}</p>
            <p>Lng: ${lng.toFixed(6)}</p>
            <button onclick="window.removeZonaMarker(${i})" class="btn btn-danger btn-sm">Eliminar</button>
          </div>
        `);
      }
    }

    this.updatePolygon();
    (window as any).removeZonaMarker = (index: number) => this.removeMarker(index);
  }

  updateCoordinateInForm(index: number, lat: number, lng: number): void {
    const coordGroup = this.coordenadas.at(index);
    if (coordGroup) {
      coordGroup.patchValue({
        latitud: lat.toString(),
        longitud: lng.toString()
      });
    }
  }

  updatePolygon(): void {
    if (this.polygon) {
      this.map.removeLayer(this.polygon);
    }

    if (this.markers.length >= 3) {
      const latlngs = this.markers.map(marker => marker.getLatLng());
      const color = this.zonaForm.get('color')?.value || '#28a745';
      
      this.polygon = L.polygon(latlngs, {
        color: color,
        weight: 2,
        opacity: 0.8,
        fillColor: color,
        fillOpacity: 0.3
      }).addTo(this.map);

      // Add popup to zona polygon
      this.polygon.bindPopup(`
        <div>
          <h6><strong>Zona</strong></h6>
          <p>Color: ${color}</p>
          <p>Puntos: ${this.markers.length}</p>
        </div>
      `);
    }
  }

  removeMarker(index: number): void {
    if (index >= 0 && index < this.markers.length) {
      // Remove marker from map
      this.map.removeLayer(this.markers[index]);
      
      // Remove from markers array
      this.markers.splice(index, 1);
      
      // Remove from form array
      this.coordenadas.removeAt(index);
      
      // Update polygon
      this.updatePolygon();
      
      // Update popup content for remaining markers
      this.updateMarkerPopups();
    }
  }

  updateMarkerPopups(): void {
    this.markers.forEach((marker, index) => {
      const latLng = marker.getLatLng();
      marker.setPopupContent(`
        <div>
          <p><strong>Punto ${index + 1}</strong></p>
          <p>Lat: ${latLng.lat.toFixed(6)}</p>
          <p>Lng: ${latLng.lng.toFixed(6)}</p>
          <button onclick="window.removeZonaMarker(${index})" class="btn btn-danger btn-sm">Eliminar</button>
        </div>
      `);
    });
  }

  clearZonaMarkers(): void {
    this.markers.forEach(marker => {
      this.map.removeLayer(marker);
    });
    this.markers = [];
    
    if (this.polygon) {
      this.map.removeLayer(this.polygon);
      this.polygon = null;
    }
  }

  clearCoordinates(): void {
    while (this.coordenadas.length !== 0) {
      this.coordenadas.removeAt(0);
    }
  }

  toggleMapView(): void {
    this.showMap = !this.showMap;
    this.showManualForm = !this.showManualForm;
    
    if (this.showMap) {
      setTimeout(() => {
        this.initializeMap();
        
        // Restore state after switching to map view
        if (this.selectedBarrio) {
          this.showBarrioOnMap();
        }
        if (this.coordenadas.length > 0) {
          this.loadMarkersFromForm();
        }
      }, 100);
    }
  }

  clearAllMarkers(): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: 'Esto eliminará todas las coordenadas de la zona',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar todo',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.clearZonaMarkers();
        this.clearCoordinates();
      }
    });
  }

  // Manual form methods (for fallback)
  addCoordenada(): void {
    this.coordenadas.push(this.createCoordenadaGroup());
  }

  removeCoordenada(index: number): void {
    this.coordenadas.removeAt(index);
    if (this.map && this.markers[index]) {
      this.removeMarker(index);
    }
  }

  onColorChange(): void {
    // Update marker colors when color changes
    if (this.map) {
      this.updateMarkerColors();
      this.updatePolygon();
    }
  }

  updateMarkerColors(): void {
    const color = this.zonaForm.get('color')?.value || '#28a745';
    this.markers.forEach(marker => {
      marker.setIcon(L.divIcon({
        className: 'zona-marker',
        html: `<div style="background-color: ${color}; width: 12px; height: 12px; border-radius: 50%; border: 2px solid white; box-shadow: 0 0 4px rgba(0,0,0,0.5);"></div>`,
        iconSize: [12, 12],
        iconAnchor: [6, 6]
      }));
    });
  }

  onSubmit(): void {
    if (this.zonaForm.valid && this.coordenadas.length >= 3) {
      const formData: Zona = this.zonaForm.value;
      const selectedBarrio = this.zonaForm.get('barrio')?.value;

      if (selectedBarrio) {
        formData.barrioId = selectedBarrio.id;
      }

      if (this.isEditMode && this.zonaId) {
        this.zonaService.update(this.zonaId, formData).subscribe(
          response => {
            Swal.fire('¡Éxito!', 'Zona actualizada con éxito.', 'success');
            this.router.navigate(['/zonas/list']);
          },
          error => {
            Swal.fire('¡Error!', 'Hubo un problema al actualizar la zona.', 'error');
          }
        );
      } else {
        this.zonaService.create(formData).subscribe(
          response => {
            Swal.fire('¡Éxito!', 'Zona creada con éxito.', 'success');
            this.router.navigate(['/zonas/list']);
          },
          error => {
            Swal.fire('¡Error!', 'Hubo un problema al crear la zona.', 'error');
          }
        );
      }
    } else {
      Swal.fire('¡Atención!', 'Por favor completa todos los campos y agrega al menos 3 coordenadas.', 'warning');
    }
  }
}