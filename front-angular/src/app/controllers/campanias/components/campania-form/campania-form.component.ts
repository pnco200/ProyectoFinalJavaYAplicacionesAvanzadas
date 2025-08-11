import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Campania } from '../../models/campania.model';
import { CampaniaService } from '../../services/campania.service';
import { BarrioService } from '../../../barrios/services/barrio.service';
import { Barrio } from '../../../barrios/models/barrio.model';
import Papa from 'papaparse';

@Component({
  selector: 'app-campania-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './campania-form.component.html',
  styleUrls: ['./campania-form.component.css']
})
export class CampaniaFormComponent implements OnInit {
  campaniaForm: FormGroup;
  isEditMode = false;
  campaniaId: number | null = null;
  barrios: Barrio[] = [];
  viviendasFile: File | null = null;
  personasFile: File | null = null;

  constructor(
    private fb: FormBuilder,
    private campaniaService: CampaniaService,
    private barrioService: BarrioService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.campaniaForm = this.fb.group({
      id: [null],
      name: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      barrioId: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.barrioService.getAll().subscribe(data => {
      this.barrios = data;
    });

    this.campaniaId = this.route.snapshot.params['id'];
    if (this.campaniaId) {
      this.isEditMode = true;
      this.campaniaService.getById(this.campaniaId).subscribe(data => {
        this.campaniaForm.patchValue(data);
      });
    }
  }

  onViviendasFileChange(event: any): void {
    this.viviendasFile = event.target.files[0];
  }

  onPersonasFileChange(event: any): void {
    this.personasFile = event.target.files[0];
  }

  onSubmit(): void {
    if (this.campaniaForm.valid) {
      const campaniaData: Campania = this.campaniaForm.value;
      if (this.isEditMode && this.campaniaId) {
        this.campaniaService.update(campaniaData).subscribe(() => {
          if (this.viviendasFile && this.personasFile) {
            this.parseFiles();
          } else {
            this.router.navigate(['/campanias']);
          }
        });
      } else {
        this.campaniaService.create(campaniaData).subscribe(() => {
          this.router.navigate(['/campanias']);
        });
      }
    }
  }

  parseFiles(): void {
    if (this.viviendasFile && this.personasFile) {
      Papa.parse(this.viviendasFile, {
        header: true,
        complete: (viviendasResult : any) => {
          const viviendasData = viviendasResult.data;
          Papa.parse(this.personasFile!, {
            header: true,
            complete: (personasResult: any) => {
              const personasData = personasResult.data;
              this.sendDataToBackend(viviendasData, personasData);
            }
          });
        }
      });
    }
  }

  sendDataToBackend(viviendasData: any[], personasData: any[]): void {
    const data = {
      viviendas: viviendasData,
      personas: personasData
    };
    this.campaniaService.uploadEncuestas(this.campaniaId!, data).subscribe(() => {
      this.router.navigate(['/campanias']);
    });
  }
}
