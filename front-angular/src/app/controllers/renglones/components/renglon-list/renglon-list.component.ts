
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { RenglonService } from '../../services/renglon.service';
import { Renglon } from '../../models/renglon.model';

@Component({
  selector: 'app-renglon-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './renglon-list.component.html',
})
export class RenglonListComponent implements OnInit {

  renglones: Renglon[] = [];

  constructor(
    private renglonService: RenglonService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadRenglones();
  }

  loadRenglones(): void {
    this.renglonService.getAll().subscribe(data => {
      this.renglones = data;
    });
  }

  editRenglon(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/renglones/edit', id]);
    }
  }

  deleteRenglon(id: number | undefined): void {
    if (id && confirm('Are you sure you want to delete this renglon?')) {
      this.renglonService.delete(id).subscribe(() => {
        this.loadRenglones(); // Refresh the list
      });
    }
  }

  viewRenglon(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/renglones/detail', id]);
    }
  }
}
