
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { EncuestaService } from '../../services/encuesta.service';
import { Encuesta } from '../../models/encuesta.model';

@Component({
  selector: 'app-encuesta-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './encuesta-detail.component.html',
})
export class EncuestaDetailComponent implements OnInit {

  encuesta: Encuesta | undefined;

  constructor(
    private route: ActivatedRoute,
    private encuestaService: EncuestaService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.encuestaService.findById(+id).subscribe(data => {
        this.encuesta = data;
      });
    }
  }
}
