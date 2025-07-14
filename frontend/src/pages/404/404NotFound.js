import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import citrus404 from "../../assets/logos/citrus404.png";
import "./404NotFound.css";

export default function NotFound404({ testMode = false }) {
  const { t } = useTranslation();
  const [seconds, setSeconds] = useState(10);
  const navigate = useNavigate();

  useEffect(() => {
    if (testMode) {
      navigate("/login");
      return;
    }
    if (seconds === 0) {
      navigate("/login");
      return;
    }
    const timer = setTimeout(() => setSeconds((s) => s - 1), 1000);
    return () => clearTimeout(timer);
  }, [seconds, navigate, testMode]);

  return (
    <div className="notfound-bg">
      <div className="notfound-content">
        <h1 className="notfound-title">{t("notfoundTitle")}</h1>
        <img src={citrus404} alt="404" className="notfound-img" />
        <div className="notfound-phrase">{t("notfoundPhrase")}</div>
        <div className="notfound-redirect">
          {t("notfoundRedirect", { segundos: seconds })}
        </div>
      </div>
    </div>
  );
}
