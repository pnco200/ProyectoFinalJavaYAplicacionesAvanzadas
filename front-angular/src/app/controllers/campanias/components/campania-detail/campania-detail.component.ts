import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Campania } from '../../models/campania.model';
import { CampaniaService } from '../../services/campania.service';

@Component({
  selector: 'app-campania-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './campania-detail.component.html',
  styleUrls: ['./campania-detail.component.css']
})
export class CampaniaDetailComponent implements OnInit {
  campania: Campania | null = null;

  constructor(
    private route: ActivatedRoute,
    private campaniaService: CampaniaService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    this.campaniaService.getById(id).subscribe(data => {
      this.campania = data;
    });
  }
}
