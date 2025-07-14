/**
 * AppraisalStateBadge module.
 * Renders a badge to visually represent the state of an appraisal (in progress, completed, closed, unknown).
 * Applies color and icon logic for each state and supports an optional dropdown indicator.
 * @module AppraisalStateBadge
 */
import { RiErrorWarningFill } from "react-icons/ri";
import { FaCircleCheck } from "react-icons/fa6";
import { FaLock } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import "./AppraisalStateBadge.css";
import { IoMdArrowDropdown } from "react-icons/io";

/**
 * AppraisalStateBadge component for displaying a badge representing the appraisal state.
 * @param {Object} props - Component props
 * @param {string} props.state - Appraisal state ("IN_PROGRESS", "COMPLETED", "CLOSED", or other)
 * @param {boolean} [props.dropdownOption] - Whether to show a dropdown indicator
 * @returns {JSX.Element} The rendered badge
 */
const AppraisalStateBadge = ({ state, dropdownOption }) => {
  const { t } = useTranslation();

  /**
   * Determines the badge class, label, and icon for the given state.
   * @param {string} state - Appraisal state
   * @returns {{ badgeClass: string, label: string, icon: JSX.Element|null }}
   */
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

  /**
   * Renders the badge with label, icon, and optional dropdown indicator.
   * @returns {JSX.Element} Badge element
   */
  return (
    <span className={badgeClass}>
      <span className="badge-label">{label}</span>
      {icon}
      {dropdownOption && <IoMdArrowDropdown />}
    </span>
  );
};

export default AppraisalStateBadge;
