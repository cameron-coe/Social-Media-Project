import { Component } from "@angular/core";
import { SignupService } from '../services/sign-up.service';
import { ModalService } from "../services/modal.service";

@Component({
    selector: 'sign-up',
    templateUrl: './sign-up.component.html',
    styleUrls: ['./login.component.css']
})
export class SignUpComponent {
    username: string = '';
    password: string = '';
    confirmPassword: string = '';

    errorMessage: string = '';

    constructor(
        private _signupService: SignupService,
        private _modalService: ModalService
    ) {}

    reset(): void {
        this.errorMessage = '';
        this.username = '';
        this.password = '';
        this.confirmPassword = '';
    }

    onSubmit(event: Event) {
        event.preventDefault();  // Prevent default form submission

        if (this.password !== this.confirmPassword) {
            // TODO: Create an actual error message
            console.error('Passwords do not match.');
            return;
        }

        const userData = {
            username: this.username,
            password: this.password
        }

        this._signupService.signUp(userData).subscribe({
            next: response => {
                console.log('Sign-up Successful!');
                console.log(response);

                // Close the Sign-up Modal
                this._modalService.setModal(null);
                this.reset();
            },
            error: error => {
                console.log('Sign-up Failed!');
                console.error(error);
                console.error(error.error.response);
                this.errorMessage = error.error.response;
            }
        });
    }

}