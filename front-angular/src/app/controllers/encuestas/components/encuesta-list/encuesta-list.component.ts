
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { EncuestaService } from '../../services/encuesta.service';
import { Encuesta } from '../../models/encuesta.model';

@Component({
  selector: 'app-encuesta-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './encuesta-list.component.html',
})
export class EncuestaListComponent implements OnInit {

  encuestas: Encuesta[] = [];

  constructor(
    private encuestaService: EncuestaService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadEncuestas();
  }

  loadEncuestas(): void {
    this.encuestaService.findAll().subscribe(data => {
      this.encuestas = data;
    });
  }

  editEncuesta(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/encuestas/edit', id]);
    }
  }

  deleteEncuesta(id: number | undefined): void {
    if (id && confirm('Are you sure you want to delete this encuesta?')) {
      this.encuestaService.delete(id).subscribe(() => {
        this.loadEncuestas(); // Refresh the list
      });
    }
  }

  viewEncuesta(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/encuestas/detail', id]);
    }
  }
}
