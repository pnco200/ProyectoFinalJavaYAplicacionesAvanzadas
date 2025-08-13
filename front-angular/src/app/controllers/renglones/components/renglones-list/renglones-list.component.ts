import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { RenglonService } from '../../services/renglon.service';
import { Chart, ChartConfiguration, registerables } from 'chart.js';

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

    this.renglonService.getChartData(uniqueEncuestaIds).subscribe({
      next: (chartData) => {
        console.log('Datos del gráfico:', chartData);
        this.chartData = chartData;
        this.showCharts = true;
        this.generateCharts();
      },
      error: (error) => {
        console.error('Error obteniendo datos del gráfico:', error);
      }
    });
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
          '#FF6384',
          '#36A2EB', 
          '#FFCE56',
          '#4BC0C0',
          '#9966FF',
          '#FF9F40',
          '#FF6384',
          '#C9CBCF',
          '#4BC0C0',
          '#FF6384'
        ],
        borderWidth: 2,
        borderColor: '#fff'
      }]
    };

    const config: ChartConfiguration = {
      type: 'pie',
      data: data,
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: `${tipoData.tipo} (Total: ${tipoData.total})`,
            font: {
              size: 16,
              weight: 'bold'
            }
          },
          legend: {
            position: 'bottom',
          },
          tooltip: {
            callbacks: {
              label: function(context: any) {
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
    this.chartData = []
  }

}
