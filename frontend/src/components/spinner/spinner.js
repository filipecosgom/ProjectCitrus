import React from "react";
import { CircularProgress, Box } from "@mui/material";

const Spinner = () => (
  <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh">
    <CircularProgress color="primary" thickness={4} />
  </Box>
);

export default Spinner;