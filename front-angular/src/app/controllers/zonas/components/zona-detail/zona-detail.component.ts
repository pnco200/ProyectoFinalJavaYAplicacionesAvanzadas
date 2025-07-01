import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Zona } from '../../models/zona.model';
import { ZonaService } from '../../services/zona.service';


@Component({
  selector: 'app-zona-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './zona-detail.component.html',
})
export class ZonaDetailComponent implements OnInit {

  zona: Zona | null = null;

  constructor(
    private route: ActivatedRoute,
    private zonaService: ZonaService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    this.zonaService.findById(id).subscribe(data => {
      if (data.length > 0) {
        this.zona = data[0];
      }
    });
  }
}
