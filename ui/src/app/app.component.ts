import {Component} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';


@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent {
    JWTPayload = 'app';


    constructor(private http: HttpClient) {
    }

    helloWorld() {
        let headers = new HttpHeaders({'JWT-Header-Payload': this.JWTPayload});
        this.http.get("http://localhost:8080/test", {headers: headers, withCredentials: true}).subscribe();
    }


    auth() {
        return this.http.get<String>("http://localhost:8080/login", {observe: 'response', withCredentials: true})
            .subscribe(resp => this.JWTPayload = resp.headers.get('JWT-Header-Payload'))
    }

}
