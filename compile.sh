#!/bin/bash

echo "Compiling Ocean Resort System..."
echo ""

# Create resort directory if it doesn't exist
mkdir -p resort

# Copy all java files to resort package
cp *.java resort/

# Compile all files
javac resort/*.java

if [ $? -eq 0 ]; then
    echo ""
    echo "========================================"
    echo "Compilation Successful!"
    echo "========================================"
    echo ""
    echo "To run the application, use:"
    echo "java resort.OceanResortSystemMain"
    echo ""
else
    echo ""
    echo "========================================"
    echo "Compilation Failed!"
    echo "========================================"
    echo "Please check the error messages above."
fi
