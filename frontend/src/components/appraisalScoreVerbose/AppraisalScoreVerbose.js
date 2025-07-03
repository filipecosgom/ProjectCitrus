import { useTranslation } from "react-i18next";
import "./AppraisalScoreVerbose.css";

const AppraisalScoreVerbose = ({ score = null, max = 4 }) => {
  const { t } = useTranslation();
  let verbose = "";
  if (score === null || score === 0) verbose = t("appraisal.scoreVerbose.null");
  else if (score === 1) verbose = t("appraisal.scoreVerbose.1");
  else if (score === 2) verbose = t("appraisal.scoreVerbose.2");
  else if (score === 3) verbose = t("appraisal.scoreVerbose.3");
  else if (score === 4) verbose = t("appraisal.scoreVerbose.4");
  else verbose = "";

  return (
    <div className='scoreStarCard-container-verbose'>
      {verbose}
    </div>
  );
};

export default AppraisalScoreVerbose;
