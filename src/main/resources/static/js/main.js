// Trend AI - Main JavaScript

// Image lazy loading handler
function handleLazyImages() {
    const lazyImages = document.querySelectorAll('img[loading="lazy"]');
    
    if ('IntersectionObserver' in window) {
        const imageObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.classList.add('loaded');
                    observer.unobserve(img);
                }
            });
        });
        
        lazyImages.forEach(img => {
            imageObserver.observe(img);
        });
    } else {
        // Fallback for browsers without IntersectionObserver
        lazyImages.forEach(img => {
            img.addEventListener('load', function() {
                this.classList.add('loaded');
            });
        });
    }
}

// Auto-dismiss alerts after 5 seconds
document.addEventListener('DOMContentLoaded', function() {
    // Initialize lazy image loading
    handleLazyImages();
    
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Add fade-in animation to cards
    const cards = document.querySelectorAll('.card');
    cards.forEach(function(card, index) {
        setTimeout(function() {
            card.classList.add('fade-in');
        }, index * 50);
    });

    // Form validation feedback
    const forms = document.querySelectorAll('.needs-validation');
    forms.forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });

    // Image preview
    const imageInputs = document.querySelectorAll('input[type="file"][accept*="image"]');
    imageInputs.forEach(function(input) {
        input.addEventListener('change', function(e) {
            const files = e.target.files;
            if (files.length > 0) {
                console.log(`${files.length} file(s) selected`);
            }
        });
    });

    // Confirm delete actions
    const deleteButtons = document.querySelectorAll('[data-confirm-delete]');
    deleteButtons.forEach(function(button) {
        button.addEventListener('click', function(e) {
            const confirmText = window.i18n?.deleteConfirm || 'Are you sure you want to delete this item?';
            if (!confirm(confirmText)) {
                e.preventDefault();
            }
        });
    });

    // Smooth scroll for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            const href = this.getAttribute('href');
            
            // Skip if href is just "#" or empty
            if (!href || href === '#' || href === '#!') {
                return;
            }
            
            e.preventDefault();
            
            try {
                const target = document.querySelector(href);
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth'
                    });
                }
            } catch (err) {
                console.warn('Invalid selector:', href);
            }
        });
    });

    // Loading state for buttons on form submit
    document.querySelectorAll('form').forEach(function(form) {
        form.addEventListener('submit', function(e) {
            const submitButton = form.querySelector('button[type="submit"]');
            if (submitButton && form.checkValidity()) {
                submitButton.disabled = true;
                const processingText = window.i18n?.processing || 'Processing...';
                submitButton.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>' + processingText;
            }
        });
    });
});

// Utility Functions
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `alert alert-${type} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3`;
    toast.style.zIndex = '9999';
    toast.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.remove();
    }, 5000);
}

function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(() => {
        const successText = window.i18n?.copiedToClipboard || 'Copied to clipboard!';
        showToast(successText, 'success');
    }).catch(err => {
        console.error('Failed to copy:', err);
        const errorText = window.i18n?.failedToCopy || 'Failed to copy';
        showToast(errorText, 'danger');
    });
}

// Export for use in other scripts
window.TrendAI = {
    showToast,
    copyToClipboard
};

