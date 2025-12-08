FROM python:3.11-slim

WORKDIR /app

# Install QuickFIX Python library
RUN pip install quickfix

# Copy the server script
COPY server.py .

# Expose the port
EXPOSE 9878

# Run the server
CMD ["python", "server.py"]
