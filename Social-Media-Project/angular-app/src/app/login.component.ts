import { Component } from "@angular/core";
import { LoginService } from "../services/login.service";
import { ModalService } from "../services/modal.service";

@Component({
    selector: 'login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent {
    username: string = '';
    password: string = '';

    hasError: boolean = false;
    errorMessage: string = '';
    
    constructor(
        private _loginService: LoginService,
        private _modalService: ModalService
    ) {}

    onSubmit(event: Event) {
        event.preventDefault();  // Prevent default form submission

        const userData = {
            username: this.username,
            password: this.password
        }

        this._loginService.login(userData).subscribe({
            next: response => {
                console.log ("----", response);
                if (response.token) {
                    console.log('Login Successful!', response);

                    const token = response.token;
                    const memberId = response.memberId;
                    const username = response.username;

                    sessionStorage.setItem('authToken', token);
                    sessionStorage.setItem('memberId', memberId);
                    sessionStorage.setItem('username', username);

                    // Close the Login Modal
                    this._modalService.setModal(null);
                } else {
                    console.error('Token not found in response.');
                }
            },
            error: error => {
                console.log('Login Failed!');
                console.log(error.status);
                console.log(error.error.error);
                console.error(error);
            }
        });
    }
}