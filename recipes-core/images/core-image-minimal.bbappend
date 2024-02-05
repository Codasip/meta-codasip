IMAGE_CMD:wic:append() {
  size="$(stat --printf=%s $out.wic)"
  echo "Size before resize $size"
  size="$(echo "x=l($size)/l(2); scale=0; 2^((x+0.5)/1)" | bc -l)"
  truncate --size="$size" "$out.wic"
  echo "Size after resize $size"
}

do_image_wic[depends] += "bc-native:do_populate_sysroot"
