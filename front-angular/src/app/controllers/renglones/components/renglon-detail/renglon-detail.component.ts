
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { RenglonService } from '../../services/renglon.service';
import { Renglon } from '../../models/renglon.model';

@Component({
  selector: 'app-renglon-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './renglon-detail.component.html',
})
export class RenglonDetailComponent implements OnInit {

  renglon: Renglon | undefined;

  constructor(
    private route: ActivatedRoute,
    private renglonService: RenglonService
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.renglonService.findById(+id).subscribe(data => {
        this.renglon = data;
      });
    }
  }
}
