import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Coordenadas } from '../../models/coordenadas.model';
import { CoordenadasService } from '../../services/coordenadas.service';

@Component({
  selector: 'app-coordenadas-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './coordenadas-list.component.html',
})
export class CoordenadasListComponent implements OnInit {

  coordenadas: Coordenadas[] = [];

  constructor(
    private coordenadasService: CoordenadasService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadCoordenadas();
  }

  loadCoordenadas(): void {
    this.coordenadasService.find().subscribe(data => {
      if (Array.isArray(data)) {
        this.coordenadas = data;
      } else {
        this.coordenadas = [data];
      }
    });
  }

  editCoordenada(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/coordenadas/edit', id]);
    }
  }

  deleteCoordenada(id: number | undefined): void {
    if (id && confirm('Are you sure you want to delete this coordinate?')) {
      this.coordenadasService.delete(id).subscribe(() => {
        this.loadCoordenadas(); // Refresh the list
      });
    }
  }

  viewCoordenada(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/coordenadas/detail', id]);
    }
  }
}
