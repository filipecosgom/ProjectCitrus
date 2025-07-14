/**
 * AppraisalScoreVerbose module.
 * Renders a verbose textual description for an appraisal score (0-4).
 * Uses i18n translation for localization.
 * @module AppraisalScoreVerbose
 */
import { useTranslation } from "react-i18next";
import "./AppraisalScoreVerbose.css";

/**
 * AppraisalScoreVerbose component for displaying a verbose description of an appraisal score.
 * @param {Object} props - Component props
 * @param {number|null} [props.score=null] - Appraisal score (0-4), or null for no score
 * @param {number} [props.max=4] - Maximum score value (not used in rendering)
 * @returns {JSX.Element} The rendered verbose score description
 */
const AppraisalScoreVerbose = ({ score = null, max = 4 }) => {
  const { t } = useTranslation();

  /**
   * Returns the verbose string for the given score using i18n translation.
   * @param {number|null} score - Appraisal score (0-4) or null
   * @returns {string} Localized verbose description
   */
  let verbose = "";
  if (score === null || score === 0) verbose = t("appraisal.scoreVerbose.null");
  else if (score === 1) verbose = t("appraisal.scoreVerbose.1");
  else if (score === 2) verbose = t("appraisal.scoreVerbose.2");
  else if (score === 3) verbose = t("appraisal.scoreVerbose.3");
  else if (score === 4) verbose = t("appraisal.scoreVerbose.4");
  else verbose = "";

  return <div className="scoreStarCard-container-verbose">{verbose}</div>;
};

export default AppraisalScoreVerbose;
