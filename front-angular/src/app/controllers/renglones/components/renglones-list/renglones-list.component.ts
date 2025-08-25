import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { RenglonService } from '../../services/renglon.service';
import { BarrioService } from '../../../barrios/services/barrio.service';
import { ZonaService } from '../../../zonas/services/zona.service';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import Swal from 'sweetalert2';
import * as L from 'leaflet';

Chart.register(...registerables);

@Component({
  selector: 'app-renglones-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './renglones-list.component.html',
  styleUrls: ['./renglones-list.component.css']
})
export class RenglonesListComponent implements OnInit {
  barrioId: number = -1;
  renglones: any[] = [];
  ubicacion: string = '';
  chartData: any[] = [];
  showCharts: boolean = false;
  showMap: boolean = false;
  map: any;
  
  // New properties for barrios and zonas
  barrio: any = null;
  zonas: any[] = [];
  barrioPolygon: L.Polygon | null = null;
  zonaPolygons: L.Polygon[] = [];

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private renglonService: RenglonService,
    private barrioService: BarrioService,
    private zonaService: ZonaService
  ) {
    // Access router state to get passed data
    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras.state) {
      this.renglones = navigation.extras.state['renglones'];
      this.ubicacion = navigation.extras.state['ubicacion'] || '';
      this.barrioId = navigation.extras.state['barrioId'] || -1;
    }
  }

  ngOnInit(): void {
    // If no data was passed via state, maybe fetch all or handle accordingly
    if (this.renglones.length === 0) {
      console.warn('No renglones data received via router state. Consider fetching all or displaying a message.');
    }

    // Load barrio and zonas data if barrioId is available
    if (this.barrioId && this.barrioId !== -1) {
      this.loadBarrioData();
      this.loadZonasData();
    }
  }

  loadBarrioData(): void {
    this.barrioService.getAll(this.barrioId).subscribe({
      next: (data) => {
        if (data.length > 0) {
          this.barrio = data[0];
        }
      },
      error: (err) => {
        console.error('Error loading barrio data:', err);
      }
    });
  }

  loadZonasData(): void {
    this.zonaService.findByBarrioId(this.barrioId).subscribe({
      next: (data) => {
        this.zonas = data;
      },
      error: (err) => {
        console.error('Error loading zonas data:', err);
      }
    });
  }

  verResultados(renglon: any): void {
    const encuestaId = renglon.encuesta_id;
    const viviendaId = renglon.vivienda_id;

    if (encuestaId) {
      this.renglonService.getRespuestas(encuestaId).subscribe({
        next: (data) => {
          this.router.navigate(['/renglones/respuestas'], { state: { respuestas: data } });
        },
        error: (err) => {
          console.error('Error al obtener respuestas:', err);
        }
      });
    } else {
      console.warn('No encuesta_id found for this renglon.');
    }
  }

  verGrafico() {
    const encuestaIds = this.renglones.map(renglon => renglon.encuesta_id);
    const uniqueEncuestaIds = [...new Set(encuestaIds)];
    
    Swal.fire({
      title: 'Espere por favor',
      didOpen: () => {
        Swal.showLoading();
      },
    });
    
    this.renglonService.getChartData(uniqueEncuestaIds).subscribe({
      next: (chartData) => {
        console.log('Datos del gráfico:', chartData);
        this.chartData = chartData;
        this.showCharts = true;
        this.generateCharts();
        Swal.fire({
          icon: 'success',
          title: 'Graficos generados con éxito',
          showConfirmButton: false,
          timer: 1500,
        });
      },
      error: (error) => {
        console.error('Error obteniendo datos del gráfico:', error);
        Swal.close();
      }
    });
  }

  verMapa() {
    this.showMap = true;
    this.showCharts = false;
    
    // Wait for DOM to update
    setTimeout(() => {
      this.initializeMap();
    }, 100);
  }

  initializeMap() {
    // Destroy existing map if exists
    if (this.map) {
      this.map.remove();
    }

    // Calculate map center based on coordinates
    const validCoords = this.renglones.filter(r => 
      r.latitud && r.longitud && 
      !isNaN(parseFloat(r.latitud)) && 
      !isNaN(parseFloat(r.longitud))
    );

    let centerLat = -34.6118; // Default Buenos Aires
    let centerLng = -58.3960;
    let zoom = 12;

    if (validCoords.length > 0) {
      const lats = validCoords.map(r => parseFloat(r.latitud));
      const lngs = validCoords.map(r => parseFloat(r.longitud));
      
      centerLat = lats.reduce((a, b) => a + b, 0) / lats.length;
      centerLng = lngs.reduce((a, b) => a + b, 0) / lngs.length;
    } else if (this.barrio && this.barrio.coordenadas.length > 0) {
      // Use barrio coordinates if no renglones coordinates
      const barrioLats = this.barrio.coordenadas.map((c: any) => parseFloat(c.latitud));
      const barrioLngs = this.barrio.coordenadas.map((c: any) => parseFloat(c.longitud));
      
      centerLat = barrioLats.reduce((a: number, b: number) => a + b, 0) / barrioLats.length;
      centerLng = barrioLngs.reduce((a: number, b: number) => a + b, 0) / barrioLngs.length;
    }

    // Initialize map
    this.map = L.map('map').setView([centerLat, centerLng], zoom);

    // Add tile layer
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    // Add barrio polygon if available
    this.addBarrioToMap();
    
    // Add zona polygons if available
    this.addZonasToMap();
    
    // Add renglones markers
    this.addRenglonesMarkers();

    // Fit bounds to show all elements
    this.fitMapBounds();
  }

  addBarrioToMap(): void {
    if (!this.barrio || !this.barrio.coordenadas) return;

    // Remove existing barrio polygon
    if (this.barrioPolygon) {
      this.map.removeLayer(this.barrioPolygon);
    }

    // Create barrio polygon
    const barrioCoords = this.barrio.coordenadas.map((coord: any) => 
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
        <h6><strong>Barrio: ${this.barrio.name}</strong></h6>
        <p>Límites del barrio seleccionado</p>
      </div>
    `);
  }

  addZonasToMap(): void {
    if (!this.zonas || this.zonas.length === 0) return;

    // Clear existing zona polygons
    this.zonaPolygons.forEach(polygon => {
      this.map.removeLayer(polygon);
    });
    this.zonaPolygons = [];

    // Add each zona
    this.zonas.forEach(zona => {
      if (zona.coordenadas && zona.coordenadas.length >= 3) {
        const zonaCoords = zona.coordenadas.map((coord: any) => 
          [parseFloat(coord.latitud.toString()), parseFloat(coord.longitud.toString())] as [number, number]
        );

        const zonaPolygon = L.polygon(zonaCoords, {
          color: zona.color || '#28a745',
          weight: 2,
          opacity: 0.8,
          fillColor: zona.color || '#28a745',
          fillOpacity: 0.3
        }).addTo(this.map);

        // Add popup to zona
        zonaPolygon.bindPopup(`
          <div>
            <h6><strong>Zona</strong></h6>
            <p><strong>Color:</strong> ${zona.color}</p>
            <p><strong>Coordenadas:</strong> ${zona.coordenadas.length}</p>
          </div>
        `);

        this.zonaPolygons.push(zonaPolygon);
      }
    });
  }

  addRenglonesMarkers(): void {
    const validCoords = this.renglones.filter(r => 
      r.latitud && r.longitud && 
      !isNaN(parseFloat(r.latitud)) && 
      !isNaN(parseFloat(r.longitud))
    );

    // Add point markers for each renglon
    validCoords.forEach(renglon => {
      const lat = parseFloat(renglon.latitud);
      const lng = parseFloat(renglon.longitud);
      
      // Color based on type
      const color = renglon.tipo === 'VIVIENDA' ? '#FF6384' : '#36A2EB';
      
      // Create point marker (not circle marker - use regular marker for points)
      const marker = L.marker([lat, lng], {
        icon: L.divIcon({
          className: 'renglon-marker',
          html: `<div style="background-color: ${color}; width: 16px; height: 16px; border-radius: 50%; border: 3px solid white; box-shadow: 0 2px 6px rgba(0,0,0,0.3);"></div>`,
          iconSize: [16, 16],
          iconAnchor: [8, 8]
        })
      }).addTo(this.map);

      // Popup with information
      const popupContent = `
        <div>
          <h6><strong>${renglon.tipo}</strong></h6>
          <p><strong>Encuesta ID:</strong> ${renglon.encuesta_id}</p>
          <p><strong>Vivienda ID:</strong> ${renglon.vivienda_id || 'N/A'}</p>
          <p><strong>Coordenadas:</strong><br>
          Lat: ${lat.toFixed(6)}<br>
          Lng: ${lng.toFixed(6)}</p>
        </div>
      `;
      
      marker.bindPopup(popupContent);
    });
  }

  fitMapBounds(): void {
    const allBounds: L.LatLngBounds[] = [];

    // Add barrio bounds
    if (this.barrioPolygon) {
      allBounds.push(this.barrioPolygon.getBounds());
    }

    // Add zona bounds
    this.zonaPolygons.forEach(polygon => {
      allBounds.push(polygon.getBounds());
    });

    // Add renglones marker bounds
    const validCoords = this.renglones.filter(r => 
      r.latitud && r.longitud && 
      !isNaN(parseFloat(r.latitud)) && 
      !isNaN(parseFloat(r.longitud))
    );

    if (validCoords.length > 0) {
      const markerBounds = L.latLngBounds(
        validCoords.map(renglon => 
          [parseFloat(renglon.latitud), parseFloat(renglon.longitud)] as [number, number]
        )
      );
      allBounds.push(markerBounds);
    }

    // Fit to combined bounds
    if (allBounds.length > 0) {
      // Create a combined bounds
      let combinedBounds = allBounds[0];
      for (let i = 1; i < allBounds.length; i++) {
        combinedBounds.extend(allBounds[i]);
      }
      this.map.fitBounds(combinedBounds.pad(0.1));
    }
  }

  getTotalLocations(): number {
    return this.renglones.filter(r => 
      r.latitud && r.longitud && 
      !isNaN(parseFloat(r.latitud)) && 
      !isNaN(parseFloat(r.longitud))
    ).length;
  }

  getTiposEncuesta(): string {
    const tipos = [...new Set(this.renglones.map(r => r.tipo))];
    return tipos.join(', ');
  }

  getBarrioName(): string {
    return this.barrio ? this.barrio.name : 'N/A';
  }

  getZonasCount(): number {
    return this.zonas.length;
  }

  generateCharts() {
    // Wait for DOM to update
    setTimeout(() => {
      this.chartData.forEach((tipoData, index) => {
        this.createPieChart(tipoData, `chart-${index}`);
      });
    }, 100);
  }

  createPieChart(tipoData: any, containerId: string) {
    const data = {
      labels: tipoData.valores.map((v: any) => v.valor),
      datasets: [{
        data: tipoData.valores.map((v: any) => v.matches),
        backgroundColor: [
          '#ff7c43',
          '#36A2EB',
          '#f95d6a',
          '#4BC0C0',
          '#9966FF',
          '#d45087',
          '#a05195',
          '#C9CBCF',
          '#003f5c',
          '#2f4b7c',
          '#ffa600',
          '#8f564b',
          '#bcbd22',
          '#17becf',
          '#7f7f7f',
          '#c7c7c7',
          '#ffbb78',
          '#98df8a',
        ],
        borderWidth: 1,
        borderColor: '#fff'
      }]
    };

    const config: ChartConfiguration = {
      type: 'pie',
      data: data,
      options: {
        responsive: true,
        maintainAspectRatio: false,
        aspectRatio: 1,
        layout: {
          padding: 10
        },
        plugins: {
          title: {
            display: true,
            text: `${tipoData.tipo} (${tipoData.total})`,
            font: {
              size: 14,
              weight: 'bold'
            },
            padding: {
              top: 5,
              bottom: 10
            }
          },
          legend: {
            position: 'bottom',
            labels: {
              font: {
                size: 11
              },
              padding: 8,
              usePointStyle: true,
              boxWidth: 12
            }
          },
          tooltip: {
            titleFont: {
              size: 12
            },
            bodyFont: {
              size: 11
            },
            callbacks: {
              label: function (context: any) {
                const label = context.label || '';
                const value = context.parsed;
                const total = context.dataset.data.reduce((a: number, b: number) => a + b, 0);
                const percentage = ((value / total) * 100).toFixed(1);
                return `${label}: ${value} (${percentage}%)`;
              }
            }
          }
        }
      }
    };

    const canvas = document.getElementById(containerId) as HTMLCanvasElement;
    if (canvas) {
      // Clear any existing chart
      Chart.getChart(canvas)?.destroy();
      new Chart(canvas, config);
    }
  }

  volverALista() {
    this.showCharts = false;
    this.showMap = false;
    this.chartData = [];
    
    // Destroy map if exists
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
    
    // Clear references
    this.barrioPolygon = null;
    this.zonaPolygons = [];
  }
}