import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Campania } from '../../models/campania.model';
import { CampaniaService } from '../../services/campania.service';
import Swal from 'sweetalert2';

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
    Swal.fire({
      title: '¿Estás seguro?',
      text: '¡No podrás revertir esto!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: '¡Sí, bórralo!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.campaniaService.delete(id).subscribe(() => {
          this.loadCampanias();
        });
      }
    });
  }
}
