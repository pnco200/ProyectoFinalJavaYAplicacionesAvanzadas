import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { Campania } from '../../models/campania.model';
import { CampaniaService } from '../../services/campania.service';
import { BarrioService } from '../../../barrios/services/barrio.service';
import { Barrio } from '../../../barrios/models/barrio.model';
import { CampaniaFiltersComponent } from '../campania-filters/campania-filters.component';
import { RenglonService } from '../../../renglones/services/renglon.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-campania-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, CampaniaFiltersComponent],
  templateUrl: './campania-detail.component.html',
  styleUrls: ['./campania-detail.component.css']
})
export class CampaniaDetailComponent implements OnInit {
  campania: Campania | null = null;
  barrio: Barrio | null = null;

  constructor(
    private route: ActivatedRoute,
    private campaniaService: CampaniaService,
    private barrioService: BarrioService,
    private renglonService: RenglonService,
    private router: Router
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    this.campaniaService.getById(id).subscribe(data => {
      this.campania = data;
      if (this.campania && this.campania.barrioId) {
        this.barrioService.getAll(this.campania.barrioId).subscribe(barrios => {
          if (barrios.length > 0) {
            this.barrio = barrios[0];
          }
        });
      }
    });
  }
/**
   * Convierte el filtro del localStorage al formato requerido por la API
   */
  private convertirFiltro(filtroString: string): any {
    try {
      // Parsear el string JSON
      const filtros = JSON.parse(filtroString);
      
      // Separar filtros por tipo de encuesta
      const filtrosVivienda: any[] = [];
      const filtrosPersona: any[] = [];
      
      filtros.forEach((filtro: any) => {
        // Convertir de "value" a "valor" y extraer solo tipo y valor
        const filtroConvertido = {
          tipo: filtro.tipo,
          valor: filtro.value
        };
        
        if (filtro.tipo_encuesta === "VIVIENDA") {
          filtrosVivienda.push(filtroConvertido);
        } else if (filtro.tipo_encuesta === "PERSONA") {
          filtrosPersona.push(filtroConvertido);
        }
      });
      
      // Crear el objeto final con null si no hay filtros de ese tipo
      return {
        filtros_vivienda: filtrosVivienda.length > 0 ? filtrosVivienda : null,
        filtros_persona: filtrosPersona.length > 0 ? filtrosPersona : null
      };
    } catch (error) {
      console.error('Error al convertir filtro:', error);
      return {
        filtros_vivienda: null,
        filtros_persona: null
      };
    }
  }
  
  verLista(): void {
    const filtroString = localStorage.getItem('filtro');
    if (filtroString) {
      // Convertir filtro al formato correcto
      const filtro = this.convertirFiltro(filtroString);
      
      this.renglonService.filtrarRenglones(filtro).subscribe({
        next: (data) => {
          this.router.navigate(['/renglones/list'], { state: { renglones: data, ubicacion:"LISTA", barrioId:this.campania!.barrioId } });
        },
        error: (err) => {
          console.error('Error al filtrar renglones:', err);
          // Handle error, e.g., show a message to the user
        }
      });
    } else {
            Swal.fire({
        icon: 'warning',
        title: 'Filtro no encontrado',
        text: 'No se pudo aplicar el filtro porque no se encontró en el almacenamiento local.',
      });
      console.warn('No se encontró el filtro en localStorage.');
      // Optionally, navigate to an empty list or show a message
    }
  }

  verGraficos(): void {
  const filtroString = localStorage.getItem('filtro');
    if (filtroString) {
      // Convertir filtro al formato correcto
      const filtro = this.convertirFiltro(filtroString);
            
      this.renglonService.filtrarRenglones(filtro).subscribe({
        next: (data) => {
          console.log('Renglones filtrados:', data);
          this.router.navigate(['/renglones/list'], { state: { renglones: data, ubicacion:"GRAFICO", barrioId:this.campania!.barrioId } });
        },
        error: (err) => {
          console.error('Error al filtrar renglones:', err);
          // Handle error, e.g., show a message to the user
        }
      });
    } else {
            Swal.fire({
        icon: 'warning',
        title: 'Filtro no encontrado',
        text: 'No se pudo aplicar el filtro porque no se encontró en el almacenamiento local.',
      });
      console.warn('No se encontró el filtro en localStorage.');
      // Optionally, navigate to an empty list or show a message
    }  }
  verMapa(): void {
const filtroString = localStorage.getItem('filtro');
    if (filtroString) {
      // Convertir filtro al formato correcto
      const filtro = this.convertirFiltro(filtroString);
      
      
      this.renglonService.filtrarRenglones(filtro).subscribe({
        next: (data) => {
          this.router.navigate(['/renglones/list'], { state: { renglones: data, ubicacion:"MAPA", barrioId:this.campania!.barrioId } });
        },
        error: (err) => {
          console.error('Error al filtrar renglones:', err);
          // Handle error, e.g., show a message to the user
        }
      });
    } else {
            Swal.fire({
        icon: 'warning',
        title: 'Filtro no encontrado',
        text: 'No se pudo aplicar el filtro porque no se encontró en el almacenamiento local.',
      });
      console.warn('No se encontró el filtro en localStorage.');
      // Optionally, navigate to an empty list or show a message
    }  }
}
