import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { RenglonService } from '../../services/renglon.service';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import Swal from 'sweetalert2';
import * as L from 'leaflet';
// Agregar en el constructor o ngOnInit
//import { icon, Marker } from 'leaflet';

//const iconRetinaUrl = 'assets/marker-icon-2x.png';
//const iconUrl = 'assets/marker-icon.png';
//const shadowUrl = 'assets/marker-shadow.png';
//const iconDefault = icon({
// iconRetinaUrl,
// iconUrl,
// shadowUrl,
// iconSize: [25, 41],
// iconAnchor: [12, 41],
// popupAnchor: [1, -34],
// tooltipAnchor: [16, -28],
// shadowSize: [41, 41]
//);
//Marker.prototype.options.icon = iconDefault;
Chart.register(...registerables);

@Component({
  selector: 'app-renglones-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './renglones-list.component.html',
  styleUrls: ['./renglones-list.component.css']
})
export class RenglonesListComponent implements OnInit {
  renglones: any[] = [];
  ubicacion: string = '';
  chartData: any[] = [];
  showCharts: boolean = false;
  showMap: boolean = false;
  map: any;
  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private renglonService: RenglonService
  ) {
    // Access router state to get passed data
    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras.state) {
      this.renglones = navigation.extras.state['renglones'];
      this.ubicacion = navigation.extras.state['ubicacion'] || '';
    }
  }

  ngOnInit(): void {
    // If no data was passed via state, maybe fetch all or handle accordingly
    if (this.renglones.length === 0) {
      console.warn('No renglones data received via router state. Consider fetching all or displaying a message.');
      // Example: Fetch all renglones if none were passed
      // this.renglonService.getAll().subscribe(data => {
      //   this.renglones = data;
      // });
    }
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
          // Handle error
        }
      });
    } else {
      console.warn('No encuesta_id found for this renglon.');
    }
  }

  // Nuevo método para ver gráfico
  verGrafico() {
    // Extraer todos los IDs de encuesta de los renglones
    const encuestaIds = this.renglones.map(renglon => renglon.encuesta_id);

    // Eliminar duplicados si los hay
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
      }
    });
  }

    verMapa() {
    this.showMap = true;
    this.showCharts = false;
    
    // Esperar un tick para que el DOM se actualice
    setTimeout(() => {
      this.initializeMap();
    }, 100);
  }
 initializeMap() {
    // Destruir mapa existente si existe
    if (this.map) {
      this.map.remove();
    }

    // Calcular centro del mapa basado en las coordenadas
    const validCoords = this.renglones.filter(r => 
      r.latitud && r.longitud && 
      !isNaN(parseFloat(r.latitud)) && 
      !isNaN(parseFloat(r.longitud))
    );

    if (validCoords.length === 0) {
      console.error('No hay coordenadas válidas para mostrar en el mapa');
      return;
    }

    const lats = validCoords.map(r => parseFloat(r.latitud));
    const lngs = validCoords.map(r => parseFloat(r.longitud));
    
    const centerLat = lats.reduce((a, b) => a + b, 0) / lats.length;
    const centerLng = lngs.reduce((a, b) => a + b, 0) / lngs.length;

    // Inicializar mapa
    this.map = L.map('map').setView([centerLat, centerLng], 12);

    // Agregar capa de tiles (OpenStreetMap)
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    // Array para almacenar todos los marcadores
    const markers: any[] = [];

    // Agregar marcadores
    validCoords.forEach(renglon => {
      const lat = parseFloat(renglon.latitud);
      const lng = parseFloat(renglon.longitud);
      
      // Color basado en el tipo
      const color = renglon.tipo === 'VIVIENDA' ? '#FF6384' : '#36A2EB';
      
      // Crear marcador personalizado
      const marker = L.circleMarker([lat, lng], {
        color: '#fff',
        weight: 2,
        fillColor: color,
        fillOpacity: 0.8,
        radius: 8
      }).addTo(this.map);

      // Popup con información
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
      markers.push(marker);
    });

    // Ajustar vista para mostrar todos los marcadores
    if (markers.length > 1) {
      const group = L.featureGroup(markers);
      this.map.fitBounds(group.getBounds().pad(0.1));
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
  generateCharts() {
    // Esperar un tick para que el DOM se actualice
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
      // Limpiar cualquier gráfico existente
      Chart.getChart(canvas)?.destroy();
      new Chart(canvas, config);
    }
  }

volverALista() {
    this.showCharts = false;
    this.showMap = false;
    this.chartData = [];
    
    // Destruir mapa si existe
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

}
