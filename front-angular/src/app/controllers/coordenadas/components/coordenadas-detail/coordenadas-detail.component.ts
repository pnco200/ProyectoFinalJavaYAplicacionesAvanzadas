import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Coordenadas } from '../../models/coordenadas.model';
import { CoordenadasService } from '../../services/coordenadas.service';

@Component({
  selector: 'app-coordenadas-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './coordenadas-detail.component.html',
})
export class CoordenadasDetailComponent implements OnInit {

  coordenada: Coordenadas | null = null;

  constructor(
    private route: ActivatedRoute,
    private coordenadasService: CoordenadasService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    this.coordenadasService.find(id).subscribe(data => {
      if (!Array.isArray(data)) {
        this.coordenada = data;
      }
    });
  }
}
