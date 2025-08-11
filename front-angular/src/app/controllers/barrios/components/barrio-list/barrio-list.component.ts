import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Barrio } from '../../models/barrio.model';
import { BarrioService } from '../../services/barrio.service';
import Swal from 'sweetalert2';


@Component({
  selector: 'app-barrio-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './barrio-list.component.html',
})
export class BarrioListComponent implements OnInit {

  barrios: Barrio[] = [];

  constructor(
    private barrioService: BarrioService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadBarrios();
  }

  loadBarrios(): void {
    this.barrioService.getAll().subscribe(data => {
      this.barrios = data;
    });
  }

  editBarrio(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/barrios/edit', id]);
    }
  }

  deleteBarrio(id: number | undefined): void {
    if (id) {
      Swal.fire({
        title: '¿Estás seguro?',
        text: '¡Se eliminarán todas las zonas y coordenadas asociadas a este barrio!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: '¡Sí, bórralo!',
        cancelButtonText: 'Cancelar'
      }).then((result) => {
        if (result.isConfirmed) {
          this.barrioService.delete(id).subscribe({
            next: () => {
              this.loadBarrios();
              Swal.fire(
                '¡Eliminado!',
                'El barrio ha sido eliminado.',
                'success'
              );
            },
            error: (error) => {
              Swal.fire(
                'Error',
                error.error.message || 'No se pudo eliminar el barrio.',
                'error'
              );
            }
          });
        }
      });
    }
  }

  viewBarrio(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/barrios/detail', id]);
    }
  }
}
