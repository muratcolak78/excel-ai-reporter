function UploadBox({ label, onFileSelect }) {
    let accept = "*/*";
    if (label.toLowerCase().includes("excel")) accept = ".xlsx";
    if (label.toLowerCase().includes("logo")) accept = "image/*";
    if (label.toLowerCase().includes("signature")) accept = "image/*";

    return (
        <div className="upload-box">
            <label className="upload-label">{label}</label>
            <input
                type="file"
                className="file-input"
                accept={accept}
                onChange={(e) => onFileSelect(e.target.files[0])}
            />
        </div>
    );
}

export default UploadBox;
