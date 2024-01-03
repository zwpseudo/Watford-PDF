#!/bin/bash

# Define the source PNG file.
SOURCE_PNG="zw.png"

# Verify that the source PNG file exists.
if [[ ! -f "$SOURCE_PNG" ]]; then
  echo "Source file $SOURCE_PNG does not exist."
  exit 1
fi

# Array of files and their respective sizes to be replaced.
declare -A PNG_FILES=(
  ["android-chrome-192x192.png"]=192
  ["android-chrome-512x512.png"]=512
  ["apple-touch-icon-114x114.png"]=114
  ["apple-touch-icon-120x120.png"]=120
  ["apple-touch-icon-144x144.png"]=144
  ["apple-touch-icon-152x152.png"]=152
  ["apple-touch-icon-180x180.png"]=180
  ["apple-touch-icon-57x57.png"]=57
  ["apple-touch-icon-60x60.png"]=60
  ["apple-touch-icon-72x72.png"]=72
  ["apple-touch-icon-76x76.png"]=76
  ["apple-touch-icon.png"]=180 # Assuming you want the largest size for the generic icon.
  ["favicon-16x16.png"]=16
  ["favicon-32x32.png"]=32
  ["mstile-150x150.png"]=150
)

# Loop through the associative array and process each file.
for file in "${!PNG_FILES[@]}"; do
  size=${PNG_FILES[$file]}
  echo "Resizing $SOURCE_PNG to ${size}x${size} and copying to $file"
  convert "$SOURCE_PNG" -resize "${size}x${size}" "$file"
done

echo "All specified PNG files have been resized and replaced."
