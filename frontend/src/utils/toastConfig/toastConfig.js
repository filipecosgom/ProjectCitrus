import { toast } from 'react-toastify';
import './toastConfig.css'


//Mensagem de sucesso
export const showSuccessToast = (message) => {
  toast.success(message, {
    className: "custom-toast-success",
    progressClassName: "custom-toast-success-progress",
    position: 'top-left',
    autoClose: 3000,
    hideProgressBar: false,
    closeOnClick: true,
    pauseOnHover: true,
    draggable: false,
  });
};

// Mensagem de erro
export const showErrorToast = (message) => {
  toast.error(message, {
    className: "custom-toast-error",
    position: 'top-left',
    autoClose: 3000,
    hideProgressBar: false,
    closeOnClick: true,
    pauseOnHover: true,
    draggable: false,
  });
};

// Mensagem de informação
export const showInfoToast = (message) => {
  toast.info(message, {
    className: "custom-toast-info",
    position: 'top-left',
    autoClose: 3000,
    hideProgressBar: false,
    closeOnClick: true,
    pauseOnHover: true,
    draggable: false,
  });
};