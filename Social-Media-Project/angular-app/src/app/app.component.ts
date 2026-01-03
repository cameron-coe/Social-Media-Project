import { Component } from '@angular/core';
import { ModalService } from '../services/modal.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'My App';
  modal: string | null = null;

  constructor (
    private _modalService: ModalService
  ) {
    this._modalService.modal$.subscribe((modal) => (this.modal = modal));
  }

  showModal(modalType: string) {
    this._modalService.setModal(modalType);
  }

  closeModal() {
    this._modalService.setModal(null);
  }

  getUsername() {
    const username = sessionStorage.getItem('username');
    return username; 
  }

  isUserLoggedIn(): boolean {
    if (sessionStorage.getItem('authToken')) {
      return true;
    }
    else {
      return false;
    }
  }

  profileClicked() {
    console.log("---Profile Clicked!");
  }
}
