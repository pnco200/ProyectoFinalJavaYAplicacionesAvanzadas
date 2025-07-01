import { Component, OnInit } from '@angular/core';
import { JornadaService } from '../services/jornada.service';
import { Jornada } from '../models/jornada';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Encuestador } from '../../encuestadores/models/encuestador';
import { EncuestadorService } from '../../encuestadores/services/encuestador.service';
import { Campania } from '../../campanias/models/campania.model';
import { CampaniaService } from '../../campanias/services/campania.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-jornadas',
  templateUrl: './jornadas.component.html',
  styleUrls: ['./jornadas.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class JornadasComponent implements OnInit {
  jornadas: Jornada[] = [];
  selectedJornada: Jornada | null = null;
  isNewJornada = false;
  allEncuestadores: Encuestador[] = [];
  jornadaEncuestadores: Encuestador[] = [];
  encuestadoresToAdd: Encuestador[] = [];
  encuestadorToRemove: Encuestador | null = null;
  campanias: Campania[] = [];
  viewingJornada: any | null = null;

  constructor(
    private jornadaService: JornadaService,
    private encuestadorService: EncuestadorService,
    private campaniaService: CampaniaService
  ) {}

  ngOnInit(): void {
    this.loadJornadas();
    this.loadEncuestadores();
    this.loadCampanias();
  }

  loadJornadas(): void {
    this.jornadaService.getJornadas().subscribe((data) => {
      this.jornadas = data;
    });
  }

  loadEncuestadores(): void {
    this.encuestadorService.getEncuestadores().subscribe((data) => {
      this.allEncuestadores = data;
    });
  }

  loadCampanias(): void {
    this.campaniaService.getAll().subscribe((data) => {
      this.campanias = data;
    });
  }

  selectJornada(jornada: Jornada): void {
    this.viewingJornada = null;
    this.selectedJornada = { ...jornada };
    this.isNewJornada = false;
    this.loadJornadaEncuestadores(jornada.id);
  }

  newJornada(): void {
    this.viewingJornada = null;
    this.selectedJornada = {
      id: 0,
      name: '',
      startDate: '',
      endDate: '',
      campaniaId: 0,
    };
    this.isNewJornada = true;
    this.jornadaEncuestadores = [];
  }

  saveJornada(): void {
    if (this.selectedJornada) {
      if (this.isNewJornada) {
        this.jornadaService.createJornada(this.selectedJornada).subscribe(() => {
          this.loadJornadas();
          this.cancel();
        });
      } else {
        this.jornadaService.updateJornada(this.selectedJornada).subscribe(() => {
          this.loadJornadas();
          this.cancel();
        });
      }
    }
  }

  deleteJornada(id: number): void {
    this.jornadaService.deleteJornada(id).subscribe(() => {
      this.loadJornadas();
      this.cancel();
    });
  }

  cancel(): void {
    this.selectedJornada = null;
    this.isNewJornada = false;
    this.jornadaEncuestadores = [];
    this.encuestadoresToAdd = [];
    this.encuestadorToRemove = null;
    this.viewingJornada = null;
  }

  loadJornadaEncuestadores(jornadaId: number): void {
    this.jornadaService.getEncuestadores(jornadaId).subscribe((data) => {
      this.jornadaEncuestadores = data;
      this.updateEncuestadoresToAdd();
    });
  }

  updateEncuestadoresToAdd(): void {
    this.encuestadoresToAdd = this.allEncuestadores.filter(
      (e) => !this.jornadaEncuestadores.some((je) => je.id === e.id)
    );
  }

  addEncuestador(encuestadorId: number): void {
    if (this.selectedJornada) {
      this.jornadaService
        .addEncuestador(this.selectedJornada.id, encuestadorId)
        .subscribe(() => {
          this.loadJornadaEncuestadores(this.selectedJornada!.id);
        });
    }
  }

  removeEncuestador(encuestadorId: number): void {
    if (this.selectedJornada) {
      this.jornadaService
        .removeEncuestador(this.selectedJornada.id, encuestadorId)
        .subscribe(() => {
          this.loadJornadaEncuestadores(this.selectedJornada!.id);
        });
    }
  }

  viewJornada(jornada: Jornada): void {
    this.selectedJornada = null;
    forkJoin({
      campania: this.campaniaService.getById(jornada.campaniaId),
      encuestadores: this.jornadaService.getEncuestadores(jornada.id)
    }).subscribe(({ campania, encuestadores }) => {
      this.viewingJornada = {
        ...jornada,
        campania,
        encuestadores
      };
    });
  }
}
