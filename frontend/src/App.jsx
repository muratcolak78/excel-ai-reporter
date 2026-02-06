import { useState } from "react";
import "./App.css";

import UploadBox from "./components/UploadBox";
import PdfPreview from "./components/PdfPreview";

function App() {
  const [excel, setExcel] = useState(null);
  const [logo, setLogo] = useState(null);
  const [sign, setSign] = useState(null);

  const [pdfUrl, setPdfUrl] = useState(null);
  const [loading, setLoading] = useState(false);

  const [companyName, setCompanyName] = useState("");
  const [preparerName, setPreparerName] = useState("");

  //const canGenerate = excel && ((logo && sign) || pdfUrl);
  const canGenerate = excel && companyName && preparerName;

  const handleGenerate = async () => {
    if (!excel) return;

    const formData = new FormData();
    formData.append("file", excel);
    formData.append("companyName", companyName);      // Şirket
    formData.append("preparerName", preparerName);    // Hazırlayan
    if (logo) formData.append("logo", logo);
    if (sign) formData.append("sign", sign);

    setLoading(true);

    try {
      const res = await fetch("http://localhost:8080/api/report/pdf", {
        method: "POST",
        body: formData,
      });

      if (!res.ok) {
        const text = await res.text();
        throw new Error(`Backend error: ${res.status} ${text}`);
      }

      const blob = await res.blob();

      // eski preview varsa memory leak olmasın
      if (pdfUrl) URL.revokeObjectURL(pdfUrl);

      const url = URL.createObjectURL(blob);
      setPdfUrl(url);
    } catch (err) {
      alert(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDownload = () => {
    if (!pdfUrl) return;

    const a = document.createElement("a");
    a.href = pdfUrl;
    a.download = "kpi-report.pdf";
    a.click();
  };

  return (
    <div className="app-container">
      {/* LEFT */}
      <div className="left-panel">
        <h2 className="title">KPI Report Generator</h2>

        <UploadBox label="Excel Import" onFileSelect={setExcel} />
        <UploadBox label="Company Logo" onFileSelect={setLogo} />
        <UploadBox label="Signature" onFileSelect={setSign} />

        <div className="name-inputs">
          <input
            type="text"
            placeholder="Unternehmen"
            value={companyName}
            onChange={(e) => setCompanyName(e.target.value)}
            className="text-input"
          /><br /><br />
          <input
            type="text"
            placeholder="Ersteller"
            value={preparerName}
            onChange={(e) => setPreparerName(e.target.value)}
            className="text-input"
          />
        </div>

        <button
          className="generate-btn"
          disabled={!canGenerate || loading}
          onClick={handleGenerate}
        >
          {loading ? "Generating..." : "Generate Report"}
        </button>

        <button
          className="generate-btn"
          style={{ marginTop: 10, background: "#2e7d32" }}
          disabled={!pdfUrl}
          onClick={handleDownload}
        >
          Download PDF
        </button>
      </div>

      {/* RIGHT */}
      <div className="right-panel">
        <PdfPreview pdfUrl={pdfUrl} />
      </div>
    </div>
  );
}

export default App;
