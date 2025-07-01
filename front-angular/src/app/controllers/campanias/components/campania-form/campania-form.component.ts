import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Campania } from '../../models/campania.model';
import { CampaniaService } from '../../services/campania.service';

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

  constructor(
    private fb: FormBuilder,
    private campaniaService: CampaniaService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.campaniaForm = this.fb.group({
      id: [null],
      name: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.campaniaId = this.route.snapshot.params['id'];
    if (this.campaniaId) {
      this.isEditMode = true;
      this.campaniaService.getById(this.campaniaId).subscribe(data => {
        this.campaniaForm.patchValue(data);
      });
    }
  }

  onSubmit(): void {
    if (this.campaniaForm.valid) {
      const campaniaData: Campania = this.campaniaForm.value;
      if (this.isEditMode && this.campaniaId) {
        this.campaniaService.update(campaniaData).subscribe(() => {
          this.router.navigate(['/campanias']);
        });
      } else {
        this.campaniaService.create(campaniaData).subscribe(() => {
          this.router.navigate(['/campanias']);
        });
      }
    }
  }
}
