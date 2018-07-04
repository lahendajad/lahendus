import {Component} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError} from "rxjs/operators";


const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': 'my-auth-token'
  })
};

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app';

  constructor(private http: HttpClient) {
  }

  getJWT() {
    this.http.get("http://localhost:8080/jwt").subscribe(
      resp => this.title = resp["test"]
    );
  }


  auth() {
    // return this.http.post<String>("localhost:8080/login", hero, httpOptions)
    //   .pipe(
    //     catchError(this.handleError('addHero', hero))
    //   );
  }

}
