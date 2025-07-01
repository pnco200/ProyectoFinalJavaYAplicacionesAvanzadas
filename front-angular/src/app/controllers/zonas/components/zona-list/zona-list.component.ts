import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ZonaService } from '../../services/zona.service';
import { Zona } from '../../models/zona.model';

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
    if (id && confirm('Are you sure you want to delete this zone?')) {
      this.zonaService.delete(id).subscribe(() => {
        this.loadZonas(); // Refresh the list
      });
    }
  }

  viewZona(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/zonas/detail', id]);
    }
  }
}
