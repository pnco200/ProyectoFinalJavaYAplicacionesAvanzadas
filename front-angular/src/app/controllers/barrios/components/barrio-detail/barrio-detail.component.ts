import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { BarrioService } from '../../services/barrio.service';
import { Barrio } from '../../models/barrio.model';

@Component({
  selector: 'app-barrio-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './barrio-detail.component.html',
})
export class BarrioDetailComponent implements OnInit {

  barrio: Barrio | null = null;

  constructor(
    private route: ActivatedRoute,
    private barrioService: BarrioService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    this.barrioService.getAll(id).subscribe(data => {
      if (data.length > 0) {
        this.barrio = data[0];
      }
    });
  }
}
