import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { RenglonService } from '../../services/renglon.service';

@Component({
  selector: 'app-renglones-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './renglones-list.component.html',
  styleUrls: ['./renglones-list.component.css']
})
export class RenglonesListComponent implements OnInit {
  renglones: any[] = [];

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private renglonService: RenglonService
  ) {
    // Access router state to get passed data
    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras.state) {
      this.renglones = navigation.extras.state['renglones'];
    }
  }

  ngOnInit(): void {
    // If no data was passed via state, maybe fetch all or handle accordingly
    if (this.renglones.length === 0) {
      console.warn('No renglones data received via router state. Consider fetching all or displaying a message.');
      // Example: Fetch all renglones if none were passed
      // this.renglonService.getAll().subscribe(data => {
      //   this.renglones = data;
      // });
    }
  }

  verResultados(renglon: any): void {
    const encuestaId = renglon.encuesta_id;
    const viviendaId = renglon.vivienda_id;

    if (encuestaId) {
      this.renglonService.getRespuestas(encuestaId).subscribe({
        next: (data) => {
          this.router.navigate(['/renglones/respuestas'], { state: { respuestas: data } });
        },
        error: (err) => {
          console.error('Error al obtener respuestas:', err);
          // Handle error
        }
      });
    } else {
      console.warn('No encuesta_id found for this renglon.');
    }
  }
}
