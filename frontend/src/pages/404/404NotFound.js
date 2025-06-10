import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import citrus404 from "../../assets/logos/citrus404.png";
import "./404NotFound.css";

export default function NotFound404() {
  const [seconds, setSeconds] = useState(10);
  const navigate = useNavigate();

  useEffect(() => {
    if (seconds === 0) {
      navigate("/login");
      return;
    }
    const timer = setTimeout(() => setSeconds((s) => s - 1), 1000);
    return () => clearTimeout(timer);
  }, [seconds, navigate]);

  return (
    <div className="notfound-bg">
      <div className="notfound-content">
        <h1 className="notfound-title">404 Not Found</h1>
        <img src={citrus404} alt="404" className="notfound-img" />
        <div className="notfound-phrase">When life gives you lemons...</div>
        <div className="notfound-redirect">
          A redirecioná-lo dentro de <span>{seconds}</span> segundos.
        </div>
      </div>
    </div>
  );
}
