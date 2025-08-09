import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './src/app/components/navbar/navbar';
import { RegisterComponent } from './controllers/users/components/register/register';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, RegisterComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected title = 'front-angular';
}

