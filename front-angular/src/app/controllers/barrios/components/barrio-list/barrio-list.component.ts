import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Barrio } from '../../models/barrio.model';
import { BarrioService } from '../../services/barrio.service';


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
    if (id && confirm('Are you sure you want to delete this barrio?')) {
      this.barrioService.delete(id).subscribe(() => {
        this.loadBarrios(); // Refresh the list
      });
    }
  }

  viewBarrio(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/barrios/detail', id]);
    }
  }
}
