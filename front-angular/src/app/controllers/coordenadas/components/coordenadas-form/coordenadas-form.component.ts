import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Coordenadas } from '../../models/coordenadas.model';
import { CoordenadasService } from '../../services/coordenadas.service';
import { Barrio } from '../../../barrios/models/barrio.model';
import { BarrioService } from '../../../barrios/services/barrio.service';
import { Zona } from '../../../zonas/models/zona.model';
import { ZonaService } from '../../../zonas/services/zona.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-coordenadas-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './coordenadas-form.component.html',
})
export class CoordenadasFormComponent implements OnInit {

  coordenadaForm: FormGroup;
  isEditMode = false;
  coordenadaId: number | null = null;
  barrios: Barrio[] = [];
  zonas: Zona[] = [];

  constructor(
    private fb: FormBuilder,
    private coordenadasService: CoordenadasService,
    private barrioService: BarrioService,
    private zonaService: ZonaService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.coordenadaForm = this.fb.group({
      latitud: ['', [Validators.required, Validators.pattern('^-?[0-9]\\d*(\\.\\d+)?$')]],
      longitud: ['', [Validators.required, Validators.pattern('^-?[0-9]\\d*(\\.\\d+)?$')]],
      barrio: [null],
      zona: [null]
    });
  }
  compareById = (a: any, b: any) => a && b && a.id === b.id;

  ngOnInit(): void {
    this.loadBarrios();
    this.loadZonas();

    this.coordenadaId = this.route.snapshot.params['id'];
    if (this.coordenadaId) {
      this.isEditMode = true;
      this.coordenadasService.find(this.coordenadaId).subscribe(data => {
        if (!Array.isArray(data)) {
          this.coordenadaForm.patchValue(data);
          if (data.barrioId) {
            this.barrioService.getAll(data.barrioId).subscribe(barr => {
              const selectedBarrio = barr.find(b => b.id === data.barrioId);
              this.coordenadaForm.get('barrio')?.setValue(selectedBarrio);
              this.coordenadaForm.get('zona')?.setValue(null); // Clear barrio if zona is set
            })
          }
          if (data.zonaId) {
            this.zonaService.findById(data.zonaId).subscribe(zonas => {
            const selectedZona = zonas.find(z => z.id === data.zonaId);
            if (selectedZona) {
              this.coordenadaForm.get('zona')?.setValue(selectedZona);
              this.coordenadaForm.get('barrio')?.setValue(null); // Clear barrio if zona is set
            }
          })
        }}
      });
    }

    this.coordenadaForm.get('barrio')?.valueChanges.subscribe(barrio => {
      if (barrio) {
        this.coordenadaForm.get('zona')?.setValue(null);
      }
    });

    this.coordenadaForm.get('zona')?.valueChanges.subscribe(zona => {
      if (zona) {
        this.coordenadaForm.get('barrio')?.setValue(null);
      }
    });
  }

  loadBarrios(): void {
    this.barrioService.getAll().subscribe(data => {
      this.barrios = data;
    });
  }

  loadZonas(): void {
    this.zonaService.findAll().subscribe(data => {
      this.zonas = data;
    });
  }

  onSubmit(): void {
    if (this.coordenadaForm.valid) {
      const formData: Coordenadas = this.coordenadaForm.value;
      const selectedBarrio = this.coordenadaForm.get('barrio')?.value;
      const selectedZona = this.coordenadaForm.get('zona')?.value;

      if (selectedBarrio) {
        formData.barrioId = selectedBarrio.id;
        formData.zonaId = undefined;
      } else if (selectedZona) {
        formData.zonaId = selectedZona.id;
        formData.barrioId = undefined;
      } else {
        formData.barrioId = undefined;
        formData.zonaId = undefined;
      }

      if (this.isEditMode && this.coordenadaId) {
        this.coordenadasService.update(this.coordenadaId, formData).subscribe(
          response => {
            Swal.fire('¡Éxito!', 'Coordenada actualizada con éxito.', 'success');
            this.router.navigate(['/coordenadas/list']);
          },
          error => {
            Swal.fire('¡Error!', 'Hubo un problema al actualizar la coordenada.', 'error');
          }
        );
      } else {
        this.coordenadasService.create(formData).subscribe(
          response => {
            Swal.fire('¡Éxito!', 'Coordenada creada con éxito.', 'success');
            this.router.navigate(['/coordenadas/list']);
          },
          error => {
            Swal.fire('¡Error!', 'Hubo un problema al crear la coordenada.', 'error');
          }
        );
      }
    }
  }
}
