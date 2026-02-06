function UploadPanel({ label, onFileSelect, accept }) {
    return (
        <div className="upload-card">
            <label className="upload-label">{label}</label>

            <input
                className="file-input"
                type="file"
                accept={accept}
                onChange={(e) => onFileSelect(e.target.files[0])}
            />
        </div>
    );
}

export default UploadPanel;
