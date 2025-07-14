import React from "react";
import { CircularProgress, Box } from "@mui/material";

/**
 * Spinner component
 *
 * Displays a centered loading spinner using Material UI.
 *
 * @returns {JSX.Element} The rendered spinner
 */
const Spinner = () => (
  <Box
    display="flex"
    justifyContent="center"
    alignItems="center"
    minHeight="80vh"
  >
    <CircularProgress color="primary" thickness={4} />
  </Box>
);

export default Spinner;
