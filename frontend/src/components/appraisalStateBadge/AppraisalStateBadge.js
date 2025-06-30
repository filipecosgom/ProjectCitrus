import { RiErrorWarningFill } from "react-icons/ri";
import { FaCircleCheck } from "react-icons/fa6";
import { FaLock } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import "./AppraisalStateBadge.css";

const AppraisalStateBadge = ({ state }) => {
  const { t } = useTranslation();
  let badgeClass = "appraisal-state-badge ";
  let label = "";
  let icon = null;

  if (state === "IN_PROGRESS") {
    badgeClass += "badge-inprogress";
    label = t("appraisalStateInProgress");
    icon = <RiErrorWarningFill className="badge-icon" />;
  } else if (state === "COMPLETED") {
    badgeClass += "badge-completed";
    label = t("appraisalStateCompleted");
    icon = <FaCircleCheck className="badge-icon" />;
  } else if (state === "CLOSED") {
    badgeClass += "badge-closed";
    label = t("appraisalStateClosed");
    icon = <FaLock className="badge-icon" />;
  } else {
    badgeClass += "badge-unknown";
    label = state;
  }

  return (
    <span className={badgeClass}>
      <span className="badge-label">{label}</span>
      {icon}
    </span>
  );
};

export default AppraisalStateBadge;
