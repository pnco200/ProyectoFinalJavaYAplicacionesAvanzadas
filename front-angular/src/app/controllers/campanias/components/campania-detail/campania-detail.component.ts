import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Campania } from '../../models/campania.model';
import { CampaniaService } from '../../services/campania.service';
import { BarrioService } from '../../../barrios/services/barrio.service';
import { Barrio } from '../../../barrios/models/barrio.model';
import { CampaniaFiltersComponent } from '../campania-filters/campania-filters.component';

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
    private barrioService: BarrioService
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

  verMapa(): void {
    // Implementar la lógica para ver el mapa
  }
  verResultados(): void {
    // Implementar la lógica para ver los resultados
  }
  verGraficos(): void {
    // Implementar la lógica para ver los gráficos
  }
}
