import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormArray } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { BarrioService } from '../../services/barrio.service';
import { Barrio } from '../../models/barrio.model';
import { Coordenadas } from '../../../coordenadas/models/coordenadas.model';
import Swal from 'sweetalert2';
import * as L from 'leaflet';

@Component({
  selector: 'app-barrio-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './barrio-form.component.html',
  styleUrls: ['./barrio-form.component.css'],
})
export class BarrioFormComponent implements OnInit, AfterViewInit, OnDestroy {

  barrioForm: FormGroup;
  isEditMode = false;
  barrioId: number | null = null;
  
  // Map related properties
  map: any;
  markers: L.Marker[] = [];
  polygon: L.Polygon | null = null;
  showMap = true;
  showManualForm = false;

  constructor(
    private fb: FormBuilder,
    private barrioService: BarrioService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.barrioForm = this.fb.group({
      nombre: ['', Validators.required],
      coordenadas: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.barrioId = this.route.snapshot.params['id'];
    if (this.barrioId) {
      this.isEditMode = true;
      this.loadBarrioData();
    }
  }

  ngAfterViewInit(): void {
    if (this.showMap) {
      setTimeout(() => {
        this.initializeMap();
      }, 100);
    }
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
    }
  }

  loadBarrioData(): void {
    this.barrioService.getAll(this.barrioId!).subscribe(data => {
      if (data.length > 0) {
        const barrio = data[0];
        this.barrioForm.patchValue({
          nombre: barrio.name
        });
        
        // Clear existing coordinates and add loaded ones
        this.clearCoordinates();
        barrio.coordenadas.forEach(coord => {
          this.coordenadas.push(this.createCoordenadaGroup(coord));
        });
        
        // If map is initialized, add markers for existing coordinates
        if (this.map) {
          this.loadMarkersFromForm();
        }
      }
    });
  }

  get coordenadas(): FormArray {
    return this.barrioForm.get('coordenadas') as FormArray;
  }

  createCoordenadaGroup(coord?: Coordenadas): FormGroup {
    return this.fb.group({
      latitud: [coord ? coord.latitud : '', [Validators.required, Validators.pattern('^-?[0-9]\\d*(\\.\\d+)?$')]],
      longitud: [coord ? coord.longitud : '', [Validators.required, Validators.pattern('^-?[0-9]\\d*(\\.\\d+)?$')]]
    });
  }

  confirmAction(): void {
    this.onSubmit(1);
  }

  initializeMap(): void {
    if (this.map) {
      this.map.remove();
    }

    // Default center (you can change this to your preferred location)
    const defaultCenter: [number, number] = [-34.6118, -58.3960]; // Buenos Aires
    
    this.map = L.map('barrio-map').setView(defaultCenter, 12);

    // Add tile layer
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    // Add click event to map
    this.map.on('dblclick ', (e: any) => {
      this.addMarker(e.latlng.lat, e.latlng.lng);
    });

    // Load existing markers if in edit mode
    if (this.isEditMode) {
      this.loadMarkersFromForm();
    }
  }

  addMarker(lat: number, lng: number): void {
    // Create marker
    const marker = L.marker([lat, lng], {
      draggable: true
    }).addTo(this.map);

    // Add marker to array
    this.markers.push(marker);
    const aux : Coordenadas = { latitud: lat, longitud: lng };
    // Add coordinate to form
    this.coordenadas.push(this.createCoordenadaGroup(aux));
    // Add drag event to marker
    marker.on('drag', (e: any) => {
      const markerIndex = this.markers.indexOf(marker);
      if (markerIndex !== -1) {
        const newLatLng = e.target.getLatLng();
        this.updateCoordinateInForm(markerIndex, newLatLng.lat, newLatLng.lng);
        this.updatePolygon();
      }
    });

    // Add popup with remove option
    marker.bindPopup(`
      <div>
        <p><strong>Coordenada ${this.markers.length}</strong></p>
        <p>Lat: ${lat.toFixed(6)}</p>
        <p>Lng: ${lng.toFixed(6)}</p>
        <button onclick="window.removeMarker(${this.markers.length - 1})" class="btn btn-danger btn-sm">Eliminar</button>
      </div>
    `);

    // Update polygon
    this.updatePolygon();

    // Make remove function available globally
    (window as any).removeMarker = (index: number) => this.removeMarker(index);
  }

  loadMarkersFromForm(): void {
    this.clearMapMarkers();
    
    for (let i = 0; i < this.coordenadas.length; i++) {
      const coord = this.coordenadas.at(i).value;
      if (coord.latitud && coord.longitud) {
        const lat = parseFloat(coord.latitud);
        const lng = parseFloat(coord.longitud);
        
        const marker = L.marker([lat, lng], {
          draggable: true
        }).addTo(this.map);

        this.markers.push(marker);

        // Add drag event
        marker.on('drag', (e: any) => {
          const markerIndex = this.markers.indexOf(marker);
          const newLatLng = e.target.getLatLng();
          this.updateCoordinateInForm(markerIndex, newLatLng.lat, newLatLng.lng);
          this.updatePolygon();
        });

        // Add popup
        marker.bindPopup(`
          <div>
            <p><strong>Coordenada ${i + 1}</strong></p>
            <p>Lat: ${lat.toFixed(6)}</p>
            <p>Lng: ${lng.toFixed(6)}</p>
            <button onclick="window.removeMarker(${i})" class="btn btn-danger btn-sm">Eliminar</button>
          </div>
        `);
      }
    }

    this.updatePolygon();
    (window as any).removeMarker = (index: number) => this.removeMarker(index);
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
      
      this.polygon = L.polygon(latlngs, {
        color: '#061427ff',
        weight: 3,
        opacity: 0.8,
        fillColor: '#131414ff',
        fillOpacity: 0.2
      }).addTo(this.map);

      // Fit map to show all markers
      const group = L.featureGroup([...this.markers, this.polygon]);
      this.map.fitBounds(group.getBounds().pad(0.1));
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
          <p><strong>Coordenada ${index + 1}</strong></p>
          <p>Lat: ${latLng.lat.toFixed(6)}</p>
          <p>Lng: ${latLng.lng.toFixed(6)}</p>
          <button onclick="window.removeMarker(${index})" class="btn btn-danger btn-sm">Eliminar</button>
        </div>
      `);
    });
  }

  clearMapMarkers(): void {
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
      }, 100);
    }
  }

  // Manual form methods (for fallback)
  addCoordenada(): void {
    this.coordenadas.push(this.createCoordenadaGroup());
  }

  removeCoordenada(index: number): void {
    this.coordenadas.removeAt(index);
  }

  centerMapOnArgentina(): void {
    if (this.map) {
      this.map.setView([-34.6118, -58.3960], 6);
    }
  }

  clearAllMarkers(): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: 'Esto eliminará todas las coordenadas marcadas',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar todo',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.clearMapMarkers();
        this.clearCoordinates();
      }
    });
  }

  onSubmit(value=0): void {
    if(value==0){return}
    if (this.barrioForm.valid && this.coordenadas.length >= 3) {
      const formData: Barrio = this.barrioForm.value;
      
      if (this.isEditMode && this.barrioId) {
        this.barrioService.update(this.barrioId, formData).subscribe(
          response => {
            Swal.fire('¡Éxito!', 'Barrio actualizado con éxito.', 'success');
            this.router.navigate(['/barrios/list']);
          },
          error => {
            Swal.fire('¡Error!', 'Hubo un problema al actualizar el barrio.', 'error');
          }
        );
      } else {
        this.barrioService.create(formData).subscribe(
          response => {
            Swal.fire('¡Éxito!', 'Barrio creado con éxito.', 'success');
            this.router.navigate(['/barrios/list']);
          },
          error => {
            Swal.fire('¡Error!', 'Hubo un problema al crear el barrio.', 'error');
          }
        );
      }
    } else {
      Swal.fire('¡Atención!', 'Por favor completa el nombre y agrega al menos 3 coordenadas.', 'warning');
    }
  }
}