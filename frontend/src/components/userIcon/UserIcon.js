import "./UserIcon.css";
import { useEffect } from "react";
import template_backup from '../../assets/photos/template_backup.png';

export default function UserIcon({ avatar, status }) {



  return (
    <div className="user-icon">
      <img
        src={avatar ? avatar : template_backup}
        alt="Profile"
      />
      <div className="status-badge">
        {status === "check" && "✔"}
        {status === "cross" && "✖"}
        {status === "stroke" && "—"}
      </div>
    </div>
  );
}
