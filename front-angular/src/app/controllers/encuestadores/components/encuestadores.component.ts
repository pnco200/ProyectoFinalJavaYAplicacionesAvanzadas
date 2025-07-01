import { Component, OnInit } from '@angular/core';
import { EncuestadorService } from '../services/encuestador.service';
import { Encuestador } from '../models/encuestador';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-encuestadores',
  templateUrl: './encuestadores.component.html',
  styleUrls: ['./encuestadores.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class EncuestadoresComponent implements OnInit {
  encuestadores: Encuestador[] = [];
  selectedEncuestador: Encuestador | null = null;
  isNewEncuestador = false;

  constructor(private encuestadorService: EncuestadorService) {}

  ngOnInit(): void {
    this.loadEncuestadores();
  }

  loadEncuestadores(): void {
    this.encuestadorService.getEncuestadores().subscribe((data) => {
      this.encuestadores = data;
    });
  }

  selectEncuestador(encuestador: Encuestador): void {
    this.selectedEncuestador = { ...encuestador };
    this.isNewEncuestador = false;
  }

  newEncuestador(): void {
    this.selectedEncuestador = {
      id: 0,
      name: '',
      dni: '',
      birthDate: '',
      sex: '',
      occupation: '',
    };
    this.isNewEncuestador = true;
  }

  saveEncuestador(): void {
    if (this.selectedEncuestador) {
      if (this.isNewEncuestador) {
        this.encuestadorService.createEncuestador(this.selectedEncuestador).subscribe(() => {
          this.loadEncuestadores();
          this.cancel();
        });
      } else {
        this.encuestadorService.updateEncuestador(this.selectedEncuestador).subscribe(() => {
          this.loadEncuestadores();
          this.cancel();
        });
      }
    }
  }

  deleteEncuestador(id: number): void {
    this.encuestadorService.deleteEncuestador(id).subscribe(() => {
      this.loadEncuestadores();
      this.cancel();
    });
  }

  cancel(): void {
    this.selectedEncuestador = null;
    this.isNewEncuestador = false;
  }
}
