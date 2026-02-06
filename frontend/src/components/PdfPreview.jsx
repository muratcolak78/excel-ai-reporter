function PdfPreview({ pdfUrl }) {

    if (!pdfUrl) {
        return (
            <div className="preview-frame"
                style={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    color: "#777"
                }}>
                PDF Preview Here
            </div>
        );
    }

    return (
        <iframe
            src={pdfUrl}
            title="PDF Preview"
            className="preview-frame"
        />
    );
}

export default PdfPreview;
