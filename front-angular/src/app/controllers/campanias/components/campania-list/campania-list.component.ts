import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Campania } from '../../models/campania.model';
import { CampaniaService } from '../../services/campania.service';

@Component({
  selector: 'app-campania-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './campania-list.component.html',
  styleUrls: ['./campania-list.component.css']
})
export class CampaniaListComponent implements OnInit {
  campanias: Campania[] = [];

  constructor(private campaniaService: CampaniaService) { }

  ngOnInit(): void {
    this.loadCampanias();
  }

  loadCampanias(): void {
    this.campaniaService.getAll().subscribe(data => {
      this.campanias = data;
    });
  }

  deleteCampania(id: number): void {
    if (confirm('¿Estás seguro de que quieres eliminar esta campaña?')) {
      this.campaniaService.delete(id).subscribe(() => {
        this.loadCampanias(); // Recargar la lista
      });
    }
  }
}
