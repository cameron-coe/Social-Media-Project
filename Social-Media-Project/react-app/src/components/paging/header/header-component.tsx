
import './header-component.css';
import LoginContainerComponent from '../../login/login-container/login-container-component';

function HeaderComponent() {
    return (
        <div className="header-component">
            <div className='align-right'>
                <LoginContainerComponent></LoginContainerComponent>
            </div>
        </div>
    )
}

export default HeaderComponent;