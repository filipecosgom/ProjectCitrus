import "./UserIcon";

export default function UserIcon(avatar, status) {
  const AVATAR_URL = "https://localhost:8443/projectcitrus/avatars/";

  return (
    <div className="user-icon">
      <img
        src={avatar ? `${AVATAR_URL}${avatar}` : `${AVATAR_URL}template.png`}
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
