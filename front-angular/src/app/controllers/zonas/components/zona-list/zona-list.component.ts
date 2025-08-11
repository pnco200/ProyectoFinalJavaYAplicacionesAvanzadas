import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ZonaService } from '../../services/zona.service';
import { Zona } from '../../models/zona.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-zona-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './zona-list.component.html',
})
export class ZonaListComponent implements OnInit {

  zonas: Zona[] = [];

  constructor(
    private zonaService: ZonaService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadZonas();
  }

  loadZonas(): void {
    this.zonaService.findAll().subscribe(data => {
      this.zonas = data;
    });
  }

  editZona(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/zonas/edit', id]);
    }
  }

  deleteZona(id: number | undefined): void {
    if (id) {
      Swal.fire({
        title: '¿Estás seguro?',
        text: '¡Se eliminarán todas las coordenadas asociadas a esta zona!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: '¡Sí, bórrala!',
        cancelButtonText: 'Cancelar'
      }).then((result) => {
        if (result.isConfirmed) {
          this.zonaService.delete(id).subscribe({
            next: () => {
              this.loadZonas();
              Swal.fire(
                '¡Eliminada!',
                'La zona ha sido eliminada.',
                'success'
              );
            },
            error: (error) => {
              Swal.fire(
                'Error',
                error.error.message || 'No se pudo eliminar la zona.',
                'error'
              );
            }
          });
        }
      });
    }
  }

  viewZona(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/zonas/detail', id]);
    }
  }
}
