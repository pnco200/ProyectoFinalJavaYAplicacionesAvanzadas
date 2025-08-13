import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-renglones-respuestas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './renglones-respuestas.component.html',
  styleUrls: ['./renglones-respuestas.component.css']
})
export class RenglonesRespuestasComponent implements OnInit {
  respuestas: any[] = [];

  constructor(private router: Router, private activatedRoute: ActivatedRoute) {
    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras.state) {
      this.respuestas = navigation.extras.state['respuestas'];
    }
  }

  ngOnInit(): void {
    if (this.respuestas.length === 0) {
      console.warn('No respuestas data received via router state.');
    }
  }

  goBack(): void {
    window.history.back();
  }
}
