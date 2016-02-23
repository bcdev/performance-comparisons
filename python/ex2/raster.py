import numpy as np

class Raster:
    def __init__(self, w, h, data, gap_count=-1):
        if w <= 0 or h <= 0 or data.size != w * h:
            raise ValueError();
        self.w = w
        self.h = h
        self.size = w * h
        self.data = data
        if gap_count >= 0:
            self.gap_count = gap_count;
        else:
            c = 0
            for i in range(data.size):
                if np.isnan(data[i]):
                    c += 1
            self.gap_count = c

    def is_singular(self):
        return self.w == 1 and self.h == 1

    def is_free_of_gaps(self):
        return self.gap_count == 0

    def is_full_of_gaps(self):
        return self.gap_count == self.size

    def clone(self):
        return Raster(self.w, self.h, np.copy(self.data), self.gap_count)