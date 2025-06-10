import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useIntl } from "react-intl";
import citrus404 from "../../assets/logos/citrus404.png";
import "./404NotFound.css";

export default function NotFound404() {
  const [seconds, setSeconds] = useState(10);
  const navigate = useNavigate();
  const intl = useIntl();

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
        <h1 className="notfound-title">
          {intl.formatMessage({ id: "notfoundTitle" })}
        </h1>
        <img src={citrus404} alt="404" className="notfound-img" />
        <div className="notfound-phrase">
          {intl.formatMessage({ id: "notfoundPhrase" })}
        </div>
        <div className="notfound-redirect">
          {intl.formatMessage(
            { id: "notfoundRedirect" },
            { segundos: seconds }
          )}
        </div>
      </div>
    </div>
  );
}
