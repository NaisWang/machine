from app import app
import product

if __name__ == '__main__':
	# product.get_product_id("vivo Y9")
	resp = product.get_report_no(37692,
															 [2567, 2023, 3445, 13009, 2125, 2118, 2114, 2134, 13787, 13791, 6982, 2067, 2129, 12604,
																2026,
																13542,
																2104, 2045, 13842, 3222, 2102, 2106, 2108, 3168, 2701, 6947, 6949, 11210])
	if resp != -1:
		product.get_price(resp)
